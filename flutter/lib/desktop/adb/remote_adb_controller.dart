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
      : assert(sessionState == null || sessionState?.sessionId == sessionId);

  Map<String, dynamic> state = {};
  bool busy = false;
  bool refreshing = false;
  bool stale = true;
  bool _disposed = false;
  bool _hasResult = false;
  String message = '正在获取 Android ADB 状态…';
  String statusError = '';
  String output = '';
  int _refreshEpoch = 0;

  bool get paired => state['paired'] == true;
  bool get connected => !stale && state['connected'] == true;
  bool get supported => !stale && state['supported'] == true;
  bool controlAllowed = true;
  bool get ready => supported && controlAllowed && !busy;
  bool get discoverySupported => (state['apiVersion'] as num? ?? 1) >= 2;
  String get summary => stale ? '状态未确认' : connected
      ? 'ADB 已连接，Shell 已验证' : paired
          ? 'Android 有配对记录，尚未连接' : 'Android 暂无配对记录';

  void _notify() { if (!_disposed) notifyListeners(); }

  Future<Map<String, dynamic>> _request(String method, [Map<String, dynamic> args = const {}]) async {
    final raw = await bind.sessionGetCommon(sessionId: sessionId,
        key: 'remote_adb_request', param: jsonEncode({'method': method, ...args}))
        .timeout(const Duration(seconds: 5));
    if (raw == null || raw.isEmpty) throw Exception('远程 ADB 不可用，请确认双方安装支持此功能的版本并保持会话连接。');
    final start = Map<String, dynamic>.from(jsonDecode(raw) as Map);
    if (start['ok'] == false) throw Exception(start['message'] ?? 'ADB 请求失败。');
    final id = start['id'] as String?;
    if (id == null || id.isEmpty) throw Exception('远程 ADB 请求无效。');
    // Eight serialized operations of at most 35s, plus a reply margin.
    final deadline = DateTime.now().add(Duration(
        seconds: method == 'status' || method == 'cancel' ? 8 : 300));
    try {
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
      throw Exception('Android 未返回 ADB 状态或结果，结果尚未确认。请保持远控连接并刷新状态；命令不会自动重放。');
    } catch (_) {
      if (!_disposed && method != 'status' && method != 'cancel') {
        // Wait for the cancellation request before allowing this window to submit
        // another operation, so a delayed owner cancellation cannot cancel it.
        try { await _request('cancel'); } catch (_) { }
      }
      rethrow;
    } finally {
      await bind.sessionGetCommon(sessionId: sessionId, key: 'remote_adb_forget', param: id)
          .timeout(const Duration(seconds: 5))
          .catchError((Object _) => null);
    }
  }

  void _apply(Map<String, dynamic> response, {int? expectedEpoch}) {
    if (response['state'] is Map) {
      state = Map<String, dynamic>.from(response['state'] as Map);
      stale = false;
      statusError = '';
      controlAllowed = response['controlAllowed'] != false;
      sessionState?.apply(state, controlAllowed, expectedEpoch: expectedEpoch);
    }
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
      _apply(response, expectedEpoch: sessionEpoch);
      if (changed && !busy) _hasResult = false;
      if (!busy && !_hasResult) message = supported ? summary : '远端不支持此无线调试模块（需要 Android 11+）。';
    } catch (error) {
      if (!_disposed && epoch == _refreshEpoch) {
        stale = true;
        statusError = _errorText(error);
        sessionState?.invalidate(expectedEpoch: sessionEpoch);
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
      message = !controlAllowed ? '当前为只读会话，不能操作 ADB。' : '请先刷新并确认 Android ADB 状态。';
      _notify();
      return null;
    }
    busy = true;
    _hasResult = true;
    _refreshEpoch++;
    sessionState?.advanceEpoch();
    final sessionEpoch = sessionState?.epoch;
    bool receivedState = false;
    message = '请求已发送，Android 将按顺序处理；其他 PC 共用同一 ADB 状态。';
    if (method == 'shell') output = '';
    _notify();
    try {
      final response = await _request(method, args);
      if (_disposed) return null;
      _refreshEpoch++;
      receivedState = response['state'] is Map;
      _apply(response, expectedEpoch: sessionEpoch);
      if (!receivedState) sessionState?.invalidate(expectedEpoch: sessionEpoch);
      sessionState?.advanceEpoch(expectedEpoch: sessionEpoch);
      if (response['ok'] != true) throw Exception(response['message'] ?? 'ADB 操作失败。');
      final result = response['result'];
      switch (method) {
        case 'pair': message = '配对成功，所有 PC 均可读取这份记录。点击“我已配对，建立连接”验证 Shell。'; break;
        case 'pairConnect': message = '配对成功，ADB 已连接，Shell 验证通过。所有 PC 共享此状态。'; break;
        case 'discover':
          final ports = result is Map ? result : const {};
          message = ports['ambiguous'] == true ? '发现多个本机端口，请核对系统显示的端口并手动填写。'
              : (ports['pairPort'] ?? 0) == 0 && (ports['connectPort'] ?? 0) == 0
                  ? '未发现端口。请开启无线调试并保持配对码窗口打开；也可手动填写。'
                  : '已扫描 Android 本机端口，已填入下方。端口会随系统配对窗口和无线调试开关变化。';
          break;
        case 'connect': output = result?.toString() ?? ''; message = 'ADB 已连接，Shell 验证成功。'; break;
        case 'shell':
          final data = result is Map ? result : const {};
          output = data['output']?.toString() ?? '';
          message = data['truncated'] == true ? '输出超过 64 KiB，已关闭命令通道；退出码未知。'
              : '命令结束，退出码：${data['exitCode']}。';
          break;
        case 'revoke': message = '本应用 ADB 身份和记录已撤销，所有 PC 的 ADB 已断开。系统列表旧条目仍需在 Android 无线调试中手动忘记。'; break;
        case 'disconnect': message = 'ADB 已断开；Android 配对记录仍保留。'; break;
      }
      return result is Map ? Map<String, dynamic>.from(result) : <String, dynamic>{};
    } catch (error) {
      if (!_disposed) {
        message = _errorText(error);
        // Query again after an uncertain outcome instead of leaving Shell ready.
        if (!receivedState) {
          sessionState?.invalidate(expectedEpoch: sessionEpoch);
          sessionState?.advanceEpoch(expectedEpoch: sessionEpoch);
        }
      }
    } finally {
      busy = false;
      _notify();
    }
    return null;
  }

  Future<void> cancel() async {
    try { await _request('cancel'); }
    catch (error) { if (!_disposed) { message = _errorText(error); _notify(); } }
  }

  String _errorText(Object error) => error.toString().replaceFirst(RegExp(r'^Exception: '), '');

  @override void dispose() {
    if (busy) {
      final epoch = sessionState?.epoch;
      // Disposal may happen while Flutter is unmounting a route.
      scheduleMicrotask(() => sessionState?.invalidate(expectedEpoch: epoch));
      // Fire only a cancellation request; never replay commands or disconnect an idle shared ADB.
      unawaited(_request('cancel').then<void>((_) {}).catchError((Object _) {}));
    }
    _disposed = true;
    super.dispose();
  }
}
