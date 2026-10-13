import 'dart:async';
import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/widgets.dart';
import 'package:uuid/uuid.dart';

import '../../models/model.dart';
import '../../models/platform_model.dart' show bind;

/// Separate media subscription; only the dedicated ADB touch capability is shared with input.
class RemoteAdbMirror extends ChangeNotifier {
  RemoteAdbMirror(this.ffi) {
    ffi.ffiModel.addListener(_peerChanged);
    _peerChanged();
  }

  final FFI ffi;
  PeerInfo? _peer;
  Timer? _timer;
  bool _disposed = false;
  bool _ready = false;
  bool _watching = false;
  bool _polling = false;
  int _epoch = 0;
  Future<void> _transition = Future<void>.value();
  Future<void> _touchTransition = Future<void>.value();
  DateTime? _commandAt;
  String phase = 'idle';
  String error = '';
  bool supported = false;
  bool screenshotSupported = false;
  String format = '';
  final ValueNotifier<bool> hasFrame = ValueNotifier(false);
  bool adbPriority = false;
  bool touchReady = false;
  String inputError = '';
  Uint8List? image;
  int _generation = 0;
  String _owner = Uuid().v4();

  bool get active => phase == 'starting' || phase == 'streaming' || phase == 'stopping';
  bool get busy => _commandAt != null || phase == 'starting' || phase == 'stopping';
  bool get screenshotMode => format == 'jpeg';
  bool get canStart => _watching && supported && (!active || screenshotMode) && !busy && !ffi.ffiModel.viewOnly;
  bool get canScreenshot => _watching && supported && screenshotSupported && (!active || !screenshotMode) && !busy && !ffi.ffiModel.viewOnly;
  bool get canStop => _watching && supported && active && phase != 'stopping' && _commandAt == null && !ffi.ffiModel.viewOnly;
  String get summary => error.isNotEmpty ? error : phase == 'starting' ? (screenshotMode ? '正在启动 ADB 连续截图…' : '正在启动 ADB 投屏…')
      : phase == 'stopping' ? '正在关闭所有 PC 的 ADB 画面…'
      : phase == 'streaming' ? (image == null ? '等待 ADB 画面…' : screenshotMode ? 'ADB 连续截图 · 多 PC 共享' : 'ADB 投屏 · 多 PC 共享')
      : phase == 'waiting' ? '正在确认投屏能力…' : 'ADB 投屏未开启';

  Future<String?> _call(String action, {String? owner}) => bind.sessionGetCommon(
      sessionId: ffi.sessionId, key: 'adb_video', param: jsonEncode({'action': action, 'owner': owner ?? _owner}));

  void _peerChanged() {
    final model = ffi.ffiModel;
    final ready = model.isPeerAndroid && model.pi.isSet.value;
    if (ready == _ready && identical(_peer, model.pi)) return;
    _peer = model.pi;
    _ready = ready;
    final epoch = ++_epoch;
    final oldOwner = _owner;
    final newOwner = _owner = Uuid().v4();
    _timer?.cancel();
    _watching = false;
    supported = false;
    screenshotSupported = false;
    format = '';
    adbPriority = false;
    ffi.inputModel.adbTouchPriority = false;
    touchReady = false;
    inputError = '';
    image = null;
    _commandAt = null;
    phase = ready ? 'waiting' : 'idle';
    error = '';
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!_disposed && epoch == _epoch) {
        hasFrame.value = phase == 'streaming' && image != null;
        notifyListeners();
      }
    });
    // Serialize old unwatch/new watch, including rapid reconnect and widget disposal.
    _transition = _transition.then((_) async {
      await _call('unwatch', owner: oldOwner);
      if (_disposed || epoch != _epoch || !ready) return;
      final result = await _call('watch', owner: newOwner);
      if (_disposed || epoch != _epoch) return;
      if (result == null) {
        phase = 'error';
        error = '无法订阅 ADB 投屏，请重新连接远程会话。';
      } else {
        _watching = true;
        // This drains the local native frame slot; it sends NO remote status queries.
        _timer = Timer.periodic(const Duration(milliseconds: 50), (_) => _poll(epoch));
      }
      if (!_disposed) notifyListeners();
    }).catchError((Object _) {
      if (!_disposed && epoch == _epoch) {
        phase = 'error';
        error = 'ADB 投屏订阅失败，请重新连接。';
        notifyListeners();
      }
    });
  }

  Future<void> _poll(int epoch) async {
    if (_disposed || _polling || !_watching || epoch != _epoch) return;
    _polling = true;
    try {
      final raw = await _call('poll');
      if (_disposed || epoch != _epoch || raw == null) return;
      final value = jsonDecode(raw) as Map<String, dynamic>;
      final next = value['state'] as String? ?? 'idle';
      final nextError = value['error'] as String? ?? '';
      final nextGeneration = (value['generation'] as num? ?? 0).toInt();
      final nextSupported = value['supported'] == true;
      final nextFormat = value['format'] as String? ?? '';
      final nextScreenshotSupported = value['screenshotSupported'] == true;
      final frame = value['image'] as String?;
      final priority = value['adbPriority'] == true;
      final ready = value['touchReady'] == true;
      final touchError = value['inputError'] as String? ?? '';
      bool changed = next != phase || nextError != error || nextGeneration != _generation || supported != nextSupported
          || format != nextFormat || screenshotSupported != nextScreenshotSupported;
      if (changed) _commandAt = null;
      changed = changed || priority != adbPriority || ready != touchReady || touchError != inputError;
      adbPriority = priority;
      ffi.inputModel.adbTouchPriority = priority;
      touchReady = ready;
      inputError = touchError;
      if (_commandAt != null && DateTime.now().difference(_commandAt!) > const Duration(seconds: 25)) {
        _commandAt = null;
        error = '投屏操作未确认，请检查连接后重试。';
        changed = true;
      } else {
        error = nextError;
      }
      if (next != 'streaming' || nextGeneration != _generation) image = null;
      phase = next;
      _generation = nextGeneration;
      supported = nextSupported;
      format = nextFormat;
      screenshotSupported = nextScreenshotSupported;
      if (frame != null && phase == 'streaming') { image = base64Decode(frame); changed = true; }
      hasFrame.value = phase == 'streaming' && image != null;
      if (changed) notifyListeners();
    } catch (_) {
      if (!_disposed && epoch == _epoch && error.isEmpty) {
        error = 'ADB 画面读取失败，请关闭投屏后重试。';
        notifyListeners();
      }
    } finally {
      _polling = false;
    }
  }

  Future<void> touch(int event, int x, int y, int width, int height) async {
    if (_disposed || !_watching || !adbPriority || !touchReady || ffi.ffiModel.viewOnly) return;
    final epoch = _epoch;
    final owner = _owner;
    // The async native bridge may use multiple workers. Preserve DOWN/MOVE/UP order.
    _touchTransition = _touchTransition.then((_) async {
      if (_disposed || epoch != _epoch || !_watching || !adbPriority || !touchReady || ffi.ffiModel.viewOnly) return;
      await bind.sessionGetCommon(sessionId: ffi.sessionId, key: 'adb_video',
        param: jsonEncode({'action': 'touch', 'owner': owner, 'event': event,
          'x': x, 'y': y, 'width': width, 'height': height}));
    }).catchError((Object _) {});
    await _touchTransition;
  }

  Future<void> setEnabled(bool enabled, {bool screenshot = false}) async {
    if (enabled ? (screenshot ? !canScreenshot : !canStart) : !canStop) return;
    _commandAt = DateTime.now();
    error = '';
    notifyListeners();
    try {
      if (await _call(enabled ? (screenshot ? 'screenshot' : 'start') : 'stop') == null) throw StateError('offline');
    } catch (_) {
      if (!_disposed) {
        _commandAt = null;
        error = '远程投屏请求未发送，请检查连接。';
        notifyListeners();
      }
    }
  }

  @override
  void dispose() {
    _disposed = true;
    ffi.inputModel.adbTouchPriority = false;
    _epoch++;
    _timer?.cancel();
    ffi.ffiModel.removeListener(_peerChanged);
    hasFrame.dispose();
    final owner = _owner;
    _transition.then((_) => _call('unwatch', owner: owner)).catchError((Object _) => null);
    super.dispose();
  }
}
