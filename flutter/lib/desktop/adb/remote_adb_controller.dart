import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import '../../common.dart';
import '../../models/platform_model.dart' show bind;
import 'remote_adb_session_state.dart';

/// Session-scoped consumer; pairing records and the ADB transport belong to Android.
class RemoteAdbController extends ChangeNotifier {
  final SessionID sessionId;
  final RemoteAdbSessionState? sessionState;
  RemoteAdbController(this.sessionId, {this.sessionState})
      : assert(sessionState == null || sessionState?.sessionId == sessionId) {
    sessionState?.addListener(_onSharedState);
    _onSharedState();
  }

  Map<String, dynamic> state = {};
  bool busy = false;
  bool refreshing = false;
  bool stale = true;
  bool _disposed = false;
  bool _hasResult = false;
  String message = '正在读取 ADB 状态…';
  String statusError = '';
  String output = '';
  int _refreshEpoch = 0;
  String? _activeRequestId;
  bool _cancelPending = false;
  Map<String, dynamic>? _sharedSnapshot;

  bool get paired => state['paired'] == true;
  bool get connected => !stale && state['connected'] == true;
  bool get supported => !stale && state['supported'] == true;
  bool get activationSupported => state['activationSupported'] == true;
  bool get reconnecting => !stale && state['activationState'] == 'reconnecting';
  bool controlAllowed = true;
  bool get ready => supported && controlAllowed && !busy;

  String get keepAliveSummary {
    if (stale || state['keepAliveSupported'] != true) return '状态未确认';
    if (state['keepAliveExempt'] == true) return '已豁免';
    if (state['keepAliveBusy'] == true) return '正在设置豁免…';
    final status = state['keepAliveExempt'] == null ? '状态未确认' : '未豁免';
    final error = state['keepAliveError'];
    return error is String && error.isNotEmpty ? '$status：$error' : status;
  }
  bool get discoverySupported => (state['apiVersion'] as num? ?? 1) >= 2;
  bool get textInputSupported => state['textInputSupported'] == true;
  static String? validatePasteText(String text) {
    if (text.isEmpty || text.contains('\u0000')) return '请输入非空且不含 NUL 的文本。';
    // Leave room for the request envelope when JSON escaping expands the text.
    if (utf8.encode(text).length > 16384 ||
        utf8.encode(jsonEncode({'method': 'paste', 'text': text})).length > 30000) {
      return '文本过长，请分段输入（单次 UTF-8 最多 16 KiB）。';
    }
    return null;
  }
  String get summary => stale ? '状态未确认' : activationSupported
      ? connected ? 'ADB 已激活'
          : state['activationState'] == 'starting' ? '正在激活 ADB…'
          : reconnecting ? '正在恢复 ADB…'
          : state['activationErrorCode'] == 'service_stopped' ? 'ADB 服务已停止 · 需重新激活'
          : state['activationErrorCode'] == 'device_rebooted' ? '手机已重启 · 需重新激活'
          : state['activationState'] == 'activation_required' ? '服务暂不可用 · 可重新激活'
          : paired ? '已配对 · 尚未激活' : '尚未配对'
      : connected
      ? '已连接 · Shell 已验证' : state['recovering'] == true
          ? '正在恢复连接…' : paired
          ? '已配对 · 尚未连接' : '尚未配对';

  void _notify() { if (!_disposed) notifyListeners(); }

  void _onSharedState() {
    final snapshot = sessionState?.snapshot;
    if (_disposed || identical(snapshot, _sharedSnapshot)) return;
    _sharedSnapshot = snapshot;
    if (snapshot == null) {
      _refreshEpoch++;
      stale = true;
    } else {
      _apply({'state': snapshot, 'controlAllowed': controlAllowed}, fromSharedState: true);
      if (!busy && !_hasResult) message = summary;
    }
    _notify();
  }

  Future<Map<String, dynamic>> _request(String method, [Map<String, dynamic> args = const {}]) async {
    final raw = await bind.sessionGetCommon(sessionId: sessionId,
        key: 'remote_adb_request', param: jsonEncode({'method': method, ...args}))
        .timeout(const Duration(seconds: 5));
    if (raw == null || raw.isEmpty) throw Exception('远程 ADB 不可用，请检查连接及两端版本。');
    final start = Map<String, dynamic>.from(jsonDecode(raw) as Map);
    if (start['ok'] == false) throw Exception(start['message'] ?? 'ADB 请求失败。');
    final id = start['id'] as String?;
    if (id == null || id.isEmpty) throw Exception('远程 ADB 请求无效。');
    final operation = method != 'status' && method != 'cancel';
    if (operation) _activeRequestId = id;
    // Eight serialized operations of at most 35s, plus a reply margin.
    final deadline = DateTime.now().add(Duration(
        seconds: method == 'status' || method == 'cancel' ? 8 : 300));
    try {
      if ((_disposed || _cancelPending) && operation) {
        _cancelPending = false;
        await _cancelRequest(id);
        if (_disposed) throw StateError('ADB controller disposed');
      }
      while (!_disposed && DateTime.now().isBefore(deadline)) {
        await Future<void>.delayed(const Duration(milliseconds: 200));
        if (_disposed) break;
        // Status and execution have independent replies. A lost status probe must
        // not orphan a queued command or discard a valid command result.
        final response = await bind.sessionGetCommon(sessionId: sessionId,
            key: 'remote_adb_poll', param: id).timeout(const Duration(seconds: 5));
        if (response == null) throw Exception('ADB 请求已失效，请刷新状态。');
        if (response.isNotEmpty) return Map<String, dynamic>.from(jsonDecode(response) as Map);
      }
      throw Exception('Android 响应超时，结果未知。请刷新状态；命令不会自动重发。');
    } catch (_) {
      if (!_disposed && operation) {
        try { await _cancelRequest(id); } catch (_) { }
      }
      rethrow;
    } finally {
      if (_activeRequestId == id) _activeRequestId = null;
      await bind.sessionGetCommon(sessionId: sessionId, key: 'remote_adb_forget', param: id)
          .timeout(const Duration(seconds: 5))
          .catchError((Object _) => null);
    }
  }

  Future<void> _cancelRequest(String id) async {
    // New Android endpoints scope cancellation to this request, not every dialog
    // and sidebar using the same PC session. Disconnect still cancels its owner.
    await _request('cancel', (state['apiVersion'] as num? ?? 1) >= 3
        ? {'requestId': id} : const {});
  }

  bool _apply(Map<String, dynamic> response, {int? expectedEpoch, bool fromSharedState = false}) {
    final snapshot = response['state'];
    if (snapshot is Map && snapshot['supported'] is bool && snapshot['connected'] is bool) {
      if (expectedEpoch != null && expectedEpoch != sessionState?.epoch && _sharedSnapshot != null
          && snapshot['instance'] != _sharedSnapshot!['instance']) return true;
      controlAllowed = response['controlAllowed'] != false;
      // A delayed reply must not overwrite a newer device-pushed connection state.
      if (!stale && snapshot['instance'] == state['instance']
          && (snapshot['revision'] as num? ?? 0) < (state['revision'] as num? ?? 0)) {
        if (!fromSharedState) sessionState?.apply(state, controlAllowed, expectedEpoch: expectedEpoch);
        return true;
      }
      state = Map<String, dynamic>.from(snapshot);
      stale = false;
      statusError = '';
      // A shared-state listener consumes the snapshot; only a real request reply
      // writes it back. Other open controllers must not feed stale permissions back.
      if (!fromSharedState) sessionState?.apply(state, controlAllowed, expectedEpoch: expectedEpoch);
      return true;
    }
    return false;
  }

  Future<void> refresh() async {
    if (_disposed || refreshing) return;
    refreshing = true;
    _notify();
    final epoch = _refreshEpoch;
    final sessionEpoch = sessionState?.epoch;
    try {
      final response = await _request('status');
      if (_disposed || epoch != _refreshEpoch) return;
      if (response['ok'] != true) throw Exception(response['message'] ?? '无法读取 ADB 状态。');
      final remoteState = response['state'];
      if (remoteState is! Map) throw Exception('Android 返回的 ADB 状态无效，请更新客户端后重试。');
      final changed = stale || state['paired'] != remoteState['paired']
          || state['connected'] != remoteState['connected'];
      if (!_apply(response, expectedEpoch: sessionEpoch)) {
        throw Exception('Android 返回的 ADB 状态无效，请更新客户端后重试。');
      }
      if (changed && !busy) _hasResult = false;
      if (!busy && !_hasResult) message = supported ? summary : '远端不支持无线配对，需要 Android 11 及以上。';
    } catch (error) {
      if (!_disposed && epoch == _refreshEpoch) {
        stale = true;
        statusError = _errorText(error);
        // A failed query is unknown, not a confirmed device disconnect.
        // Keep the last confirmed sidebar state; this controller stays stale.
        if (!busy) _hasResult = false;
      }
    } finally {
      refreshing = false;
      _notify();
    }
  }

  Future<Map<String, dynamic>?> run(String method, [Map<String, dynamic> args = const {}]) async {
    if (_disposed || busy) return null;
    if (!supported || !controlAllowed) {
      message = !controlAllowed ? '只读会话，无法操作 ADB。' : '请先刷新 ADB 状态。';
      _notify();
      return null;
    }
    busy = true;
    _cancelPending = false;
    _hasResult = true;
    _refreshEpoch++;
    sessionState?.advanceEpoch();
    final sessionEpoch = sessionState?.epoch;
    bool receivedState = false;
    message = '正在处理…';
    if (method == 'shell') output = '';
    _notify();
    try {
      final response = await _request(method, args);
      if (_disposed) return null;
      _refreshEpoch++;
      receivedState = _apply(response, expectedEpoch: sessionEpoch);
      if (!receivedState) stale = true;
      sessionState?.advanceEpoch(expectedEpoch: sessionEpoch);
      if (response['ok'] != true) throw Exception(response['message'] ?? 'ADB 操作失败。');
      final result = response['result'];
      switch (method) {
        case 'pair': message = activationSupported
            ? '配对已保存，请点击“激活 ADB”。'
            : '配对已保存，请点击“连接 ADB”。'; break;
        case 'pairConnect': message = activationSupported
            ? '配对成功，ADB 已激活。'
            : '配对成功，Shell 已验证。'; break;
        case 'discover':
          final ports = result is Map ? result : const {};
          message = ports['ambiguous'] == true ? '发现多个端口，请按手机显示手动填写。'
              : (ports['pairPort'] ?? 0) == 0 && (ports['connectPort'] ?? 0) == 0
                  ? '未发现端口，请保持无线调试和配对码窗口打开，或手动填写。'
                  : '端口已填入；重新打开无线调试或配对窗口后请再扫描。';
          break;
        case 'connect': output = result?.toString() ?? ''; message = activationSupported
            ? 'ADB 已激活' : '已连接 · Shell 已验证'; break;
        case 'paste': message = '文本已发送，请查看手机输入框。'; break;
        case 'accessibilityEnable':
        case 'accessibilityDisable':
        case 'accessibilityPause':
        case 'accessibilityResume':
          message = result is Map && result['message'] is String
              ? result['message'] as String : '请以 Android 返回的无障碍状态为准。';
          break;
        case 'shell':
          final data = result is Map ? result : const {};
          output = data['output']?.toString() ?? '';
          message = data['truncated'] == true ? '输出超过 64 KiB，已关闭命令通道；退出码未知。'
              : '命令结束，退出码：${data['exitCode']}。';
          break;
        case 'revoke': message = '配对已撤销，所有 PC 的 ADB 已停用。手机系统旧记录需手动忘记。'; break;
        case 'disconnect': message = activationSupported
            ? 'ADB 已停用，配对保留；下次需再激活。' : 'ADB 已断开，配对记录保留。'; break;
      }
      return result is Map ? Map<String, dynamic>.from(result) : <String, dynamic>{};
    } catch (error) {
      if (!_disposed) {
        message = _errorText(error);
        // Require a new query for this controller without hiding the whole sidebar.
        if (!receivedState) {
          stale = true;
          statusError = message;
          sessionState?.advanceEpoch(expectedEpoch: sessionEpoch);
        }
      }
    } finally {
      busy = false;
      _cancelPending = false;
      _notify();
    }
    return null;
  }

  Future<void> cancel() async {
    final id = _activeRequestId;
    if (id == null) { _cancelPending = busy; return; }
    try { await _cancelRequest(id); }
    catch (error) { if (!_disposed) { message = _errorText(error); _notify(); } }
  }

  String _errorText(Object error) => error.toString().replaceFirst(RegExp(r'^Exception: '), '');

  @override void dispose() {
    sessionState?.removeListener(_onSharedState);
    final id = _activeRequestId;
    if (busy && id != null) {
      // Fire only a cancellation request; never replay commands or disconnect an idle shared ADB.
      unawaited(_cancelRequest(id).catchError((Object _) {}));
    }
    _disposed = true;
    super.dispose();
  }
}
