import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import '../../common.dart';
import '../../models/platform_model.dart' show bind;

/// Session-scoped consumer; pairing records and the ADB transport belong to Android.
class RemoteAdbController extends ChangeNotifier {
  final SessionID sessionId;
  RemoteAdbController(this.sessionId);

  Map<String, dynamic> state = {};
  bool busy = false;
  bool refreshing = false;
  bool stale = true;
  bool _disposed = false;
  bool _hasResult = false;
  Timer? _timer;
  String message = '正在获取 Android ADB 状态…';
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

  Future<void> watch(bool enabled) async {
    _timer?.cancel();
    if (enabled) {
      stale = true;
      _hasResult = false;
      if (!busy) message = '正在获取 Android ADB 状态…';
      _timer = Timer.periodic(const Duration(seconds: 3), (_) => refresh());
      await refresh();
    }
  }

  void _notify() { if (!_disposed) notifyListeners(); }

  Future<Map<String, dynamic>> _request(String method, [Map<String, dynamic> args = const {}]) async {
    final raw = await bind.sessionGetCommon(sessionId: sessionId,
        key: 'remote_adb_request', param: jsonEncode({'method': method, ...args}));
    if (raw == null || raw.isEmpty) throw Exception('远程 ADB 不可用，请确认双方安装支持此功能的版本并保持会话连接。');
    final start = Map<String, dynamic>.from(jsonDecode(raw) as Map);
    if (start['ok'] == false) throw Exception(start['message'] ?? 'ADB 请求失败。');
    final id = start['id'] as String?;
    if (id == null || id.isEmpty) throw Exception('远程 ADB 请求无效。');
    // Android accepts at most 8 operations; QR pairing waits at most 90s per item.
    final deadline = DateTime.now().add(Duration(seconds: method == 'status' ? 8 : 740));
    try {
      while (!_disposed && DateTime.now().isBefore(deadline)) {
        await Future<void>.delayed(const Duration(milliseconds: 200));
        if (_disposed) break;
        if (stale && method != 'status' && method != 'cancel') {
          throw Exception('与 Android 的 ADB 状态通信已中断，执行结果尚未确认。恢复连接后请先刷新状态；不会自动重复执行命令。');
        }
        final response = await bind.sessionGetCommon(sessionId: sessionId,
            key: 'remote_adb_poll', param: id);
        if (response == null) throw Exception('ADB 请求已失效，请刷新状态。');
        if (response.isNotEmpty) return Map<String, dynamic>.from(jsonDecode(response) as Map);
      }
      throw Exception('Android 未返回 ADB 状态或结果。请确认手机和 PC 都已更新，并保持远控连接；可刷新重试。命令不会自动重放。');
    } finally {
      await bind.sessionGetCommon(sessionId: sessionId, key: 'remote_adb_forget', param: id)
          .catchError((Object _) => null);
    }
  }

  void _apply(Map<String, dynamic> response) {
    if (response['state'] is Map) {
      state = Map<String, dynamic>.from(response['state'] as Map);
      stale = false;
      controlAllowed = response['controlAllowed'] != false;
    }
  }

  Future<void> refresh() async {
    if (_disposed || refreshing) return;
    refreshing = true;
    final epoch = _refreshEpoch;
    try {
      final response = await _request('status');
      if (_disposed || epoch != _refreshEpoch) return;
      _apply(response);
      if (response['ok'] != true) throw Exception(response['message'] ?? '无法读取 ADB 状态。');
      if (!busy && !_hasResult) message = supported ? summary : '远端不支持此无线调试模块（需要 Android 11+）。';
    } catch (error) {
      if (!_disposed && epoch == _refreshEpoch) {
        stale = true;
        if (!busy) { _hasResult = false; message = _errorText(error); }
      }
    } finally {
      refreshing = false;
      _notify();
    }
  }

  Future<Map<String, dynamic>?> run(String method, [Map<String, dynamic> args = const {}]) async {
    if (_disposed || busy) return null;
    busy = true;
    _hasResult = true;
    _refreshEpoch++;
    message = '请求已发送，Android 将按顺序处理；其他 PC 共用同一 ADB 状态。';
    if (method == 'shell') output = '';
    _notify();
    try {
      final response = await _request(method, args);
      if (_disposed) return null;
      _refreshEpoch++;
      _apply(response);
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
      if (!_disposed) message = _errorText(error);
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
    _timer?.cancel();
    if (busy) {
      // Fire only a cancellation request; never replay commands or disconnect an idle shared ADB.
      unawaited(_request('cancel').then<void>((_) {}).catchError((Object _) {}));
    }
    _disposed = true;
    super.dispose();
  }
}
