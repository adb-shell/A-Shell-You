import 'dart:async';
import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/widgets.dart';
import 'package:uuid/uuid.dart';

import '../../models/model.dart';
import '../../models/platform_model.dart' show bind;
import 'remote_adb_session_state.dart';

/// Separate media subscription; only the dedicated ADB touch capability is shared with input.
class RemoteAdbMirror extends ChangeNotifier {
  RemoteAdbMirror(this.ffi, {this.sessionState}) {
    ffi.ffiModel.addListener(_peerChanged);
    _peerChanged();
  }

  final FFI ffi;
  final RemoteAdbSessionState? sessionState;
  PeerInfo? _peer;
  Timer? _timer;
  bool _disposed = false;
  bool _ready = false;
  bool _watching = false;
  bool _polling = false;
  int _epoch = 0;
  int _watchFailures = 0;
  Future<void> _transition = Future<void>.value();
  final List<Map<String, dynamic>> _touchQueue = [];
  bool _touchSending = false;
  int _touchSequence = 0;
  int _controlSequence = 0;
  int _videoRequest = 0, _privacyRequest = 0;
  int _stateRevision = 0;
  String? _remoteInstance;
  bool? _desiredEnabled, _desiredPrivacyEnabled;
  String _desiredFormat = '';
  Timer? _commandDeadline;
  Timer? _privacyDeadline;
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
  final ValueNotifier<int> frameUpdates = ValueNotifier(0);
  bool adbPriority = false;
  bool touchReady = false;
  String inputError = '';
  Uint8List? image;
  int rgbaWidth = 0, rgbaHeight = 0;
  int _generation = 0;
  int get frameGeneration => _generation;
  String _owner = Uuid().v4();

  bool get active => phase == 'starting' || phase == 'streaming' || phase == 'stopping';
  bool get screenshotMode => format == 'jpeg';
  bool get layoutMode => format == 'layout';
  bool _canStartMode(String target) => _watching && supported && !ffi.ffiModel.viewOnly &&
      (_desiredEnabled != null ? _desiredEnabled == false || _desiredFormat != target
          : phase == 'stopping' || !active || (format.isEmpty ? 'h264' : format) != target);
  bool _canStopMode(String target) => _watching && supported && !ffi.ffiModel.viewOnly &&
      (_desiredEnabled != null ? _desiredEnabled == true && _desiredFormat == target
          : (active || error.isNotEmpty) && (format.isEmpty ? 'h264' : format) == target);
  bool get canStart => _canStartMode('h264');
  bool get canScreenshot => screenshotSupported && _canStartMode('jpeg');
  bool get canLayout => layoutSupported && _canStartMode('layout');
  bool get canStopVideo => _canStopMode('h264');
  bool get canStopScreenshot => _canStopMode('jpeg');
  bool get canStopLayout => _canStopMode('layout');
  // Opposite intents stay available during teardown; Android retains one latest target.
  bool get canBlackOn => _watching && privacySupported && adbPriority && !ffi.ffiModel.viewOnly &&
      (_desiredPrivacyEnabled != null ? _desiredPrivacyEnabled == false
          : privacyPhase != 'starting' && privacyPhase != 'on');
  bool get canBlackOff => _watching && privacySupported && _desiredPrivacyEnabled != false && !ffi.ffiModel.viewOnly;
  String get privacySummary => privacyError.isNotEmpty ? privacyError
      : privacyPhase == 'on' ? '手机黑色遮罩已开启；PC 继续接收底层画面'
      : privacyPhase == 'starting' ? '正在建立手机黑色遮罩…'
      : privacyPhase == 'stopping' ? '正在移除手机黑色遮罩…' : '手机黑色遮罩未开启';
  String get summary => error.isNotEmpty ? error : phase == 'starting' ? (layoutMode ? '正在读取界面布局…' : screenshotMode ? '正在启动连续截图…' : '正在启动投屏…')
      : phase == 'stopping' ? '正在关闭所有 PC 的 ADB 画面…'
      : phase == 'streaming' ? (image == null ? '等待画面…' : '画面传输中')
      : phase == 'waiting' ? '正在确认画面能力…' : 'ADB 画面未开启';

  Future<String?> _call(String action, {String? owner, int? sequence}) => bind.sessionGetCommon(
      sessionId: ffi.sessionId, key: 'adb_video', param: jsonEncode({'action': action, 'owner': owner ?? _owner,
        if (sequence != null) 'sequence': sequence}))
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
    _watchFailures = 0;
    _polling = false;
    _commandDeadline?.cancel();
    _privacyDeadline?.cancel();
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
    _controlSequence = 0;
    _videoRequest = 0; _privacyRequest = 0;
    _stateRevision = 0;
    _remoteInstance = null;
    _desiredEnabled = null; _desiredPrivacyEnabled = null;
    _desiredFormat = '';
    format = '';
    adbPriority = false;
    ffi.inputModel.adbTouchPriority = false;
    touchReady = false;
    inputError = '';
    image = null;
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
      await _subscribe(epoch, newOwner);
    }).catchError((Object _) => _retrySubscription(epoch));
  }

  Future<void> _subscribe(int epoch, String owner) async {
    if (_disposed || epoch != _epoch || !_ready) return;
    if (await _call('watch', owner: owner) == null) throw StateError('ADB watch unavailable');
    if (_disposed || epoch != _epoch) return;
    _watching = true;
    _watchFailures = 0;
    _requestError = '';
    _schedulePoll(epoch);
    notifyListeners();
  }

  void _retrySubscription(int epoch) {
    if (_disposed || epoch != _epoch || !_ready) return;
    _watching = false;
    phase = 'error';
    _requestError = 'ADB 通讯暂时中断，正在恢复订阅…';
    _watchFailures = (_watchFailures + 1).clamp(1, 5).toInt();
    _timer?.cancel();
    _timer = Timer(Duration(seconds: 1 << _watchFailures), () {
      _transition = _transition.then((_) => _subscribe(epoch, _owner))
          .catchError((Object _) => _retrySubscription(epoch));
    });
    notifyListeners();
  }

  void _schedulePoll(int epoch) {
    _timer = Timer(Duration(milliseconds: phase == 'streaming' ? 16 : 100), () async {
      await _poll(epoch);
      if (!_disposed && epoch == _epoch && _watching) _schedulePoll(epoch);
    });
  }

  Future<void> _poll(int epoch) async {
    if (_disposed || _polling || !_watching || epoch != _epoch) return;
    _polling = true;
    try {
      final raw = await _call('poll');
      if (_disposed || epoch != _epoch) return;
      if (raw == null) { _retrySubscription(epoch); return; }
      final value = jsonDecode(raw) as Map<String, dynamic>;
      final adbState = value['adbState'];
      final instance = adbState is Map ? adbState['instance'] as String? : null;
      if (_remoteInstance != null && instance != null && instance != _remoteInstance) {
        _desiredEnabled = null; _desiredPrivacyEnabled = null;
        _commandDeadline?.cancel(); _privacyDeadline?.cancel();
        _touchQueue.clear();
        _requestError = ''; _privacyRequestError = '';
      }
      if (instance != null) _remoteInstance = instance;
      if (adbState is Map) sessionState?.acceptSnapshot(Map<String, dynamic>.from(adbState));
      final next = value['state'] as String? ?? 'idle';
      final nextError = value['error'] as String? ?? '';
      final nextGeneration = (value['generation'] as num? ?? 0).toInt();
      final nextRevision = (value['revision'] as num? ?? 0).toInt();
      final newState = nextRevision != _stateRevision;
      _stateRevision = nextRevision;
      final nextSupported = value['supported'] == true;
      final nextFormat = value['format'] as String? ?? '';
      final nextScreenshotSupported = value['screenshotSupported'] == true;
      final nextLayoutSupported = value['layoutSupported'] == true;
      final nextPrivacySupported = value['privacySupported'] == true;
      final nextPrivacyPhase = value['privacyState'] as String? ?? 'idle';
      final nextPrivacyError = value['privacyError'] as String? ?? '';
      final frame = value['image'] as String?;
      // Verified device Shell owns priority even while the private touch transport restarts.
      final priority = adbState is Map ? adbState['supported'] == true && adbState['connected'] == true
          : value['adbPriority'] == true;
      final ready = value['touchReady'] == true;
      final touchError = value['inputError'] as String? ?? '';
      bool changed = next != phase || nextError != _remoteError || nextGeneration != _generation || supported != nextSupported
          || format != nextFormat || screenshotSupported != nextScreenshotSupported || layoutSupported != nextLayoutSupported;
      // An idempotent stop can reply with the SAME idle state. Acknowledge an
      // actual state event, not an unchanged cached local poll.
      final videoAcknowledged = newState && _desiredEnabled != null && (next == 'error' || nextError.isNotEmpty ||
          (_desiredEnabled == true ? nextFormat == _desiredFormat && (next == 'starting' || next == 'streaming')
              : next == 'idle' || next == 'stopping'));
      if (videoAcknowledged || (changed && _desiredEnabled == null)) {
        _desiredEnabled = null;
        _commandDeadline?.cancel(); _requestError = '';
        changed = true;
      }
      changed = changed || priority != adbPriority || ready != touchReady || touchError != inputError;
      final privacyAcknowledged = newState && _desiredPrivacyEnabled != null && (nextPrivacyPhase == 'error' ||
          (_desiredPrivacyEnabled == true ? nextPrivacyPhase == 'starting' || nextPrivacyPhase == 'on'
              : nextPrivacyPhase == 'idle' || nextPrivacyPhase == 'stopping'));
      final privacyChanged = privacyPhase != nextPrivacyPhase || _privacyRemoteError != nextPrivacyError || privacySupported != nextPrivacySupported;
      if (privacyAcknowledged || (privacyChanged && _desiredPrivacyEnabled == null)) {
        _desiredPrivacyEnabled = null;
        _privacyDeadline?.cancel();
        _privacyRequestError = '';
        changed = true;
      }
      changed = changed || privacyChanged;
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
        rgbaWidth = (value['rgbaWidth'] as num? ?? 0).toInt();
        rgbaHeight = (value['rgbaHeight'] as num? ?? 0).toInt();
        sourceWidth = (value['sourceWidth'] as num? ?? 0).toInt();
        sourceHeight = (value['sourceHeight'] as num? ?? 0).toInt();
      }
      hasFrame.value = phase == 'streaming' && image != null;
      if (changed) notifyListeners();
      // Frame delivery must not rebuild the sidebar, URL field or other controls.
      else if (frame != null && phase == 'streaming') frameUpdates.value++;
    } catch (_) {
      _retrySubscription(epoch);
    } finally {
      if (epoch == _epoch) _polling = false;
    }
  }

  Future<void> touch(int event, int x, int y, int width, int height) async {
    if (_disposed || !_watching || !adbPriority || ffi.ffiModel.viewOnly) return;
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
        if (!_watching || !adbPriority || ffi.ffiModel.viewOnly) { _touchQueue.clear(); break; }
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
    final target = layout ? 'layout' : screenshot ? 'jpeg' : 'h264';
    if (enabled ? (layout ? !canLayout : screenshot ? !canScreenshot : !canStart) : !_canStopMode(target)) return;
    final epoch = _epoch;
    final request = _videoRequest = ++_controlSequence;
    _desiredEnabled = enabled;
    _desiredFormat = target;
    _requestError = '';
    _commandDeadline?.cancel();
    _commandDeadline = Timer(const Duration(seconds: 25), () {
      if (_disposed || epoch != _epoch || request != _videoRequest) return;
      _desiredEnabled = null;
      _requestError = 'ADB 画面操作未确认，可以再次点击关闭后重试。';
      notifyListeners();
    });
    notifyListeners();
    try {
      final scopedStop = (sessionState?.snapshot?['mirrorVersion'] as num? ?? 0) >= 2;
      final action = enabled ? (layout ? 'layout' : screenshot ? 'screenshot' : 'start')
          : scopedStop ? (layout ? 'stop_layout' : screenshot ? 'stop_screenshot' : 'stop_video') : 'stop';
      if (await _call(action, sequence: request) == null) throw StateError('offline');
    } catch (_) {
      if (!_disposed && epoch == _epoch && request == _videoRequest && _desiredEnabled != null) {
        _desiredEnabled = null;
        _commandDeadline?.cancel();
        _requestError = '远程投屏请求未发送，请检查连接。';
        notifyListeners();
      }
    }
  }

  Future<void> setBlack(bool enabled) async {
    if (enabled ? !canBlackOn : !canBlackOff) return;
    final epoch = _epoch;
    final request = _privacyRequest = ++_controlSequence;
    _desiredPrivacyEnabled = enabled;
    _privacyRequestError = '';
    _privacyDeadline?.cancel();
    _privacyDeadline = Timer(const Duration(seconds: 30), () {
      if (_disposed || epoch != _epoch || request != _privacyRequest) return;
      _desiredPrivacyEnabled = null;
      _privacyRequestError = '黑屏操作未确认，请点击关黑屏重试。';
      notifyListeners();
    });
    notifyListeners();
    try {
      if (await _call(enabled ? 'black_on' : 'black_off', sequence: request) == null) throw StateError('offline');
    } catch (_) {
      if (!_disposed && epoch == _epoch && request == _privacyRequest && _desiredPrivacyEnabled != null) {
        _desiredPrivacyEnabled = null;
        _privacyDeadline?.cancel();
        _privacyRequestError = '黑屏请求未发送，请检查连接。';
        notifyListeners();
      }
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
    frameUpdates.dispose();
    final owner = _owner;
    _transition.then((_) => _call('unwatch', owner: owner)).catchError((Object _) => null);
    super.dispose();
  }
}
