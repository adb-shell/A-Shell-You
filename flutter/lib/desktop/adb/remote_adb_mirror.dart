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
  final List<Map<String, dynamic>> _touchQueue = [];
  bool _touchSending = false;
  int _touchSequence = 0;
  DateTime? _commandAt;
  Timer? _commandDeadline;
  Timer? _privacyDeadline;
  bool _stopSending = false;
  bool _privacySending = false;
  bool _privacyPending = false;
  String phase = 'idle';
  String _remoteError = '';
  String _requestError = '';
  String get error => _requestError.isNotEmpty ? _requestError : _remoteError;
  bool supported = false;
  bool screenshotSupported = false;
  bool layoutSupported = false;
  int sourceWidth = 0, sourceHeight = 0;
  String format = '';
  bool privacySupported = false;
  String privacyPhase = 'idle';
  String _privacyRemoteError = '';
  String _privacyRequestError = '';
  String get privacyError => _privacyRequestError.isNotEmpty ? _privacyRequestError : _privacyRemoteError;
  final ValueNotifier<bool> hasFrame = ValueNotifier(false);
  bool adbPriority = false;
  bool touchReady = false;
  String inputError = '';
  Uint8List? image;
  int _generation = 0;
  int get frameGeneration => _generation;
  String _owner = Uuid().v4();

  bool get active => phase == 'starting' || phase == 'streaming' || phase == 'stopping';
  bool get busy => _commandAt != null || phase == 'starting' || phase == 'stopping';
  bool get screenshotMode => format == 'jpeg';
  bool get layoutMode => format == 'layout';
  bool get videoMode => format == 'h264' || format.isEmpty;
  bool get canStart => _watching && supported && (!active || !videoMode) && !busy && !_stopSending && !ffi.ffiModel.viewOnly;
  bool get canScreenshot => _watching && supported && screenshotSupported && (!active || !screenshotMode) && !busy && !_stopSending && !ffi.ffiModel.viewOnly;
  bool get canLayout => _watching && supported && layoutSupported && (!active || !layoutMode) && !busy && !_stopSending && !ffi.ffiModel.viewOnly;
  // Stop must remain a recovery path while startup/teardown is pending.
  bool get canStop => _watching && supported && (active || _commandAt != null || error.isNotEmpty)
      && !_stopSending && !ffi.ffiModel.viewOnly;
  bool get canBlackOn => _watching && privacySupported && adbPriority && !_privacyPending && !_privacySending
      && privacyPhase != 'starting' && privacyPhase != 'stopping' && privacyPhase != 'on' && !ffi.ffiModel.viewOnly;
  bool get canBlackOff => _watching && privacySupported && !_privacySending && !ffi.ffiModel.viewOnly;
  String get privacySummary => privacyError.isNotEmpty ? privacyError
      : privacyPhase == 'on' ? '手机黑色遮罩已开启；PC 继续接收底层画面'
      : privacyPhase == 'starting' ? '正在建立手机黑色遮罩…'
      : privacyPhase == 'stopping' ? '正在移除手机黑色遮罩…' : '手机黑色遮罩未开启';
  String get summary => error.isNotEmpty ? error : phase == 'starting' ? (layoutMode ? '正在读取界面布局…' : screenshotMode ? '正在启动连续截图…' : '正在启动投屏…')
      : phase == 'stopping' ? '正在关闭所有 PC 的 ADB 画面…'
      : phase == 'streaming' ? (image == null ? '等待画面…' : '画面传输中')
      : phase == 'waiting' ? '正在确认画面能力…' : 'ADB 画面未开启';

  Future<String?> _call(String action, {String? owner}) => bind.sessionGetCommon(
      sessionId: ffi.sessionId, key: 'adb_video', param: jsonEncode({'action': action, 'owner': owner ?? _owner}))
      .timeout(const Duration(seconds: 5));

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
    _polling = false;
    _commandDeadline?.cancel();
    _privacyDeadline?.cancel();
    _stopSending = false;
    _privacySending = false;
    _privacyPending = false;
    privacySupported = false;
    privacyPhase = 'idle';
    _privacyRemoteError = '';
    _privacyRequestError = '';
    supported = false;
    screenshotSupported = false;
    layoutSupported = false;
    sourceWidth = 0; sourceHeight = 0;
    _touchQueue.clear();
    _touchSequence = 0;
    format = '';
    adbPriority = false;
    ffi.inputModel.adbTouchPriority = false;
    touchReady = false;
    inputError = '';
    image = null;
    _commandAt = null;
    phase = ready ? 'waiting' : 'idle';
    _remoteError = '';
    _requestError = '';
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
        _requestError = '无法订阅 ADB 投屏，请重新连接远程会话。';
      } else {
        _watching = true;
        // This drains the local native frame slot; it sends NO remote status queries.
        _timer = Timer.periodic(const Duration(milliseconds: 50), (_) => _poll(epoch));
      }
      if (!_disposed) notifyListeners();
    }).catchError((Object _) {
      if (!_disposed && epoch == _epoch) {
        phase = 'error';
        _requestError = 'ADB 投屏订阅失败，请重新连接。';
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
      final nextLayoutSupported = value['layoutSupported'] == true;
      final nextPrivacySupported = value['privacySupported'] == true;
      final nextPrivacyPhase = value['privacyState'] as String? ?? 'idle';
      final nextPrivacyError = value['privacyError'] as String? ?? '';
      final frame = value['image'] as String?;
      final priority = value['adbPriority'] == true;
      final ready = value['touchReady'] == true;
      final touchError = value['inputError'] as String? ?? '';
      bool changed = next != phase || nextError != _remoteError || nextGeneration != _generation || supported != nextSupported
          || format != nextFormat || screenshotSupported != nextScreenshotSupported || layoutSupported != nextLayoutSupported;
      if (changed) { _commandAt = null; _commandDeadline?.cancel(); _requestError = ''; }
      changed = changed || priority != adbPriority || ready != touchReady || touchError != inputError;
      if (privacyPhase != nextPrivacyPhase || _privacyRemoteError != nextPrivacyError || privacySupported != nextPrivacySupported) {
        _privacyPending = false;
        _privacyDeadline?.cancel();
        _privacyRequestError = '';
        changed = true;
      }
      privacySupported = nextPrivacySupported;
      privacyPhase = nextPrivacyPhase;
      _privacyRemoteError = nextPrivacyError;
      adbPriority = priority;
      ffi.inputModel.adbTouchPriority = priority;
      touchReady = ready;
      inputError = touchError;
      _remoteError = nextError;
      if (next != 'streaming' || nextGeneration != _generation) image = null;
      phase = next;
      _generation = nextGeneration;
      supported = nextSupported;
      format = nextFormat;
      screenshotSupported = nextScreenshotSupported;
      layoutSupported = nextLayoutSupported;
      if (frame != null && phase == 'streaming') {
        image = base64Decode(frame);
        sourceWidth = (value['sourceWidth'] as num? ?? 0).toInt();
        sourceHeight = (value['sourceHeight'] as num? ?? 0).toInt();
        changed = true;
      }
      hasFrame.value = phase == 'streaming' && image != null;
      if (changed) notifyListeners();
    } catch (_) {
      if (!_disposed && epoch == _epoch && error.isEmpty) {
        _requestError = 'ADB 画面读取失败，请关闭投屏后重试。';
        notifyListeners();
      }
    } finally {
      if (epoch == _epoch) _polling = false;
    }
  }

  Future<void> touch(int event, int x, int y, int width, int height) async {
    if (_disposed || !_watching || !adbPriority || !touchReady || ffi.ffiModel.viewOnly) return;
    final packet = <String, dynamic>{'action': 'touch', 'owner': _owner, 'sequence': ++_touchSequence, 'event': event,
      'x': x, 'y': y, 'width': width, 'height': height};
    // Coalesce adjacent MOVE only. An unbounded future chain made drags lag behind the screen.
    if (event == 2 && _touchQueue.isNotEmpty && _touchQueue.last['event'] == 2) _touchQueue.removeLast();
    if (_touchQueue.length >= 32) {
      _touchQueue.clear();
      packet['event'] = 1; // release instead of losing UP on overload
    }
    _touchQueue.add(packet);
    await _drainTouches();
  }

  Future<void> _drainTouches() async {
    if (_touchSending) return;
    _touchSending = true;
    final epoch = _epoch;
    Map<String, dynamic>? sending;
    try {
      while (_touchQueue.isNotEmpty && !_disposed && epoch == _epoch) {
        if (!_watching || !adbPriority || !touchReady || ffi.ffiModel.viewOnly) { _touchQueue.clear(); break; }
        final next = _touchQueue.removeAt(0);
        sending = next;
        final result = await bind.sessionGetCommon(sessionId: ffi.sessionId, key: 'adb_video', param: jsonEncode(next))
            .timeout(const Duration(seconds: 2));
        if (result == null) throw StateError('touch unavailable');
      }
    } catch (_) {
      if (!_disposed && epoch == _epoch) {
        _touchQueue.clear();
        if (sending != null && !ffi.ffiModel.viewOnly) {
          // Higher sequence also rejects a late native DOWN after its timeout.
          final release = {...sending, 'event': 1, 'sequence': ++_touchSequence};
          try {
            await bind.sessionGetCommon(sessionId: ffi.sessionId, key: 'adb_video', param: jsonEncode(release))
                .timeout(const Duration(seconds: 2));
          } catch (_) { }
        }
      }
    } finally {
      _touchSending = false;
      // Reconnect may have enqueued an event while the old native call was completing.
      if (!_disposed && _touchQueue.isNotEmpty) _drainTouches();
    }
  }

  Future<void> setEnabled(bool enabled, {bool screenshot = false, bool layout = false}) async {
    if (enabled ? (layout ? !canLayout : screenshot ? !canScreenshot : !canStart) : !canStop) return;
    final epoch = _epoch;
    _stopSending = !enabled;
    _commandAt = enabled || active ? DateTime.now() : null;
    _requestError = '';
    _commandDeadline?.cancel();
    _commandDeadline = Timer(const Duration(seconds: 25), () {
      if (_disposed || epoch != _epoch) return;
      _commandAt = null;
      _requestError = 'ADB 画面操作未确认，可以再次点击关闭后重试。';
      notifyListeners();
    });
    if (_commandAt == null) _commandDeadline?.cancel(); // idempotent stop of an already-idle source
    notifyListeners();
    try {
      if (await _call(enabled ? (layout ? 'layout' : screenshot ? 'screenshot' : 'start') : 'stop') == null) throw StateError('offline');
    } catch (_) {
      if (!_disposed && epoch == _epoch) {
        _commandAt = null;
        _commandDeadline?.cancel();
        _requestError = '远程投屏请求未发送，请检查连接。';
        notifyListeners();
      }
    } finally {
      if (!_disposed && epoch == _epoch) { _stopSending = false; notifyListeners(); }
    }
  }

  Future<void> setBlack(bool enabled) async {
    if (enabled ? !canBlackOn : !canBlackOff) return;
    final epoch = _epoch;
    _privacySending = true;
    _privacyPending = enabled || privacyPhase != 'idle';
    _privacyRequestError = '';
    _privacyDeadline?.cancel();
    _privacyDeadline = Timer(const Duration(seconds: 30), () {
      if (_disposed || epoch != _epoch) return;
      _privacyPending = false;
      _privacyRequestError = '黑屏操作未确认，请点击关黑屏重试。';
      notifyListeners();
    });
    if (!_privacyPending) _privacyDeadline?.cancel(); // an unchanged idle snapshot is a valid no-op
    notifyListeners();
    try {
      if (await _call(enabled ? 'black_on' : 'black_off') == null) throw StateError('offline');
    } catch (_) {
      if (!_disposed && epoch == _epoch) {
        _privacyPending = false;
        _privacyDeadline?.cancel();
        _privacyRequestError = '黑屏请求未发送，请检查连接。';
      }
    } finally {
      if (!_disposed && epoch == _epoch) { _privacySending = false; notifyListeners(); }
    }
  }

  @override
  void dispose() {
    _disposed = true;
    _touchQueue.clear();
    ffi.inputModel.adbTouchPriority = false;
    _epoch++;
    _timer?.cancel();
    _commandDeadline?.cancel();
    _privacyDeadline?.cancel();
    ffi.ffiModel.removeListener(_peerChanged);
    hasFrame.dispose();
    final owner = _owner;
    _transition.then((_) => _call('unwatch', owner: owner)).catchError((Object _) => null);
    super.dispose();
  }
}
