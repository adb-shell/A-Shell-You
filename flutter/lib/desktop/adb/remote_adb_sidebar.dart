import 'dart:math' as math;

import 'package:flutter/material.dart';

import '../../common.dart';
import '../../consts.dart';
import '../../models/model.dart';
import 'remote_adb_controller.dart';
import 'remote_adb_session_state.dart';
import 'remote_adb_mirror.dart';

/// ADB navigation has its own position, controller and command path.
class RemoteAdbSidebar extends StatefulWidget {
  const RemoteAdbSidebar({super.key, required this.ffi, required this.state, required this.mirror});

  final FFI ffi;
  final RemoteAdbSessionState state;
  final RemoteAdbMirror mirror;

  @override
  State<RemoteAdbSidebar> createState() => _RemoteAdbSidebarState();
}

class _RemoteAdbSidebarState extends State<RemoteAdbSidebar> {
  late final RemoteAdbController _adb;
  PeerInfo? _peer;
  bool _peerReady = false;
  bool _viewOnly = false;
  bool _probePending = false;
  bool _sending = false;
  bool _peerUpdateScheduled = false;
  bool? _pushedPriority;
  Offset? _position;

  @override
  void initState() {
    super.initState();
    _adb = RemoteAdbController(widget.ffi.sessionId, sessionState: widget.state);
    widget.ffi.ffiModel.addListener(_schedulePeerUpdate);
    widget.mirror.addListener(_applyTouchState);
    _schedulePeerUpdate();
  }

  void _applyTouchState() {
    if (!mounted) return;
    if (!widget.mirror.supported) { _pushedPriority = null; return; }
    if (_pushedPriority != widget.mirror.adbPriority) {
      _pushedPriority = widget.mirror.adbPriority;
      // An older in-flight status reply must not overwrite the device push.
      widget.state.advanceEpoch();
    }
    widget.state.apply({'supported': true, 'connected': widget.mirror.adbPriority},
        !widget.ffi.ffiModel.viewOnly);
  }

  void _schedulePeerUpdate() {
    if (_peerUpdateScheduled) return;
    _peerUpdateScheduled = true;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _peerUpdateScheduled = false;
      if (!mounted) return;
      final model = widget.ffi.ffiModel;
      final ready = model.isPeerAndroid && model.pi.isSet.value;
      final newPeer = !identical(_peer, model.pi);
      final shouldRefresh = ready &&
          (!_peerReady || newPeer || (_viewOnly && !model.viewOnly));
      if (newPeer) {
        widget.state.updatePeer(ready: false, viewOnly: model.viewOnly);
      }
      _peer = model.pi;
      _peerReady = ready;
      _viewOnly = model.viewOnly;
      widget.state.updatePeer(ready: ready, viewOnly: model.viewOnly);
      // One query when an Android session becomes ready; no periodic polling.
      _probePending = ready && (_probePending || shouldRefresh);
      if (_probePending && !_adb.refreshing && !_adb.busy) {
        _probePending = false;
        _adb.refresh();
      }
    });
  }

  Future<void> _navigate(int keyCode) async {
    if (_sending || _adb.busy || _adb.refreshing || !widget.state.available ||
        widget.ffi.ffiModel.viewOnly) return;
    setState(() => _sending = true);
    try {
      // Confirm the transport before sending a key; never pair/reconnect here.
      await _adb.refresh();
      if (!mounted) return;
      if (!_adb.connected || !_adb.ready) {
        showToast(_adb.statusError.isNotEmpty ? _adb.statusError : _adb.summary);
        return;
      }
      if (!widget.state.visible || widget.ffi.ffiModel.viewOnly ||
          ModalRoute.of(context)?.isCurrent == false) return;
      final result = await _adb.run('shell', {'command': 'input keyevent $keyCode'});
      if (!mounted) return;
      if (result == null || result['exitCode'] != 0) showToast(_adb.message);
    } finally {
      if (mounted) setState(() => _sending = false);
    }
  }

  @override
  void dispose() {
    widget.ffi.ffiModel.removeListener(_schedulePeerUpdate);
    widget.mirror.removeListener(_applyTouchState);
    _adb.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: Listenable.merge([widget.state, _adb, widget.mirror,
        widget.ffi.ffiModel, widget.ffi.canvasModel]),
      builder: (context, _) {
        _schedulePeerUpdate();
        if (!widget.state.visible || !widget.ffi.ffiModel.isPeerAndroid ||
            !widget.ffi.ffiModel.pi.isSet.value || widget.ffi.ffiModel.viewOnly) {
          return const SizedBox.shrink();
        }
        return LayoutBuilder(builder: (context, constraints) {
          if (constraints.maxWidth <= 0 || constraints.maxHeight <= 0) {
            return const SizedBox.shrink();
          }
          final scale = math.min(
              math.max(widget.ffi.canvasModel.scale * 2.0, 0.85),
              math.min(constraints.maxWidth / 70,
                  constraints.maxHeight * 0.9 / (45 * 12)));
          final width = 70.0 * scale;
          final height = 290.0 * scale;
          Offset clamp(Offset value) => Offset(
              value.dx.clamp(0.0, math.max(0.0, constraints.maxWidth - width)).toDouble(),
              value.dy.clamp(0.0, math.max(0.0, constraints.maxHeight - height)).toDouble());
          final position = clamp(_position ?? Offset(
              constraints.maxWidth * 0.25 + 90 * scale,
              constraints.maxHeight * 0.1));
          final enabled = !_sending && !_adb.busy && !_adb.refreshing;
          Widget button(IconData icon, String label, int keyCode) => IconButton(
              tooltip: label,
              color: Colors.white,
              disabledColor: Colors.white38,
              splashRadius: kDesktopIconButtonSplashRadius,
              icon: Icon(icon), iconSize: 24,
              onPressed: enabled ? () => _navigate(keyCode) : null);
          return Stack(children: [
            Positioned(left: position.dx, top: position.dy, width: width, height: height,
              child: GestureDetector(
                onPanUpdate: (event) => setState(() {
                  _position = clamp(clamp(_position ?? position) + event.delta);
                }),
                child: FittedBox(child: SizedBox(width: 70, height: 290,
                  child: Card(
                    color: Colors.transparent,
                    shadowColor: Colors.transparent,
                    child: Container(
                      decoration: BoxDecoration(
                        color: MyTheme.accent.withOpacity(0.4),
                        borderRadius: BorderRadius.circular(15),
                      ),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        children: [
                          const Padding(
                            padding: EdgeInsets.symmetric(horizontal: 4),
                            child: FittedBox(fit: BoxFit.scaleDown,
                              child: Text('Shell栏', style: TextStyle(
                                  color: Colors.white, fontSize: 12))),
                          ),
                          button(Icons.arrow_back, 'ADB 返回', 4),
                          button(Icons.home, 'ADB 主页', 3),
                          button(Icons.more_horiz, 'ADB 多任务', 187),
                          _mirrorButton(Icons.screen_share_outlined, '开投屏', widget.mirror.canStart,
                              () => widget.mirror.setEnabled(true)),
                          _mirrorButton(Icons.stop_screen_share_outlined, '关投屏', widget.mirror.canStop,
                              () => widget.mirror.setEnabled(false)),
                        ],
                      ),
                    ),
                  ),
                )),
              ),
            ),
          ]);
        });
      },
    );
  }

  Widget _mirrorButton(IconData icon, String label, bool enabled, VoidCallback action) => Tooltip(
    message: widget.mirror.inputError.isNotEmpty ? widget.mirror.inputError
        : widget.mirror.error.isNotEmpty ? widget.mirror.error
        : label == '关投屏' ? '关闭所有 PC 的 ADB 投屏' : widget.mirror.summary,
    child: TextButton(onPressed: enabled ? action : null,
      style: TextButton.styleFrom(foregroundColor: Colors.white, disabledForegroundColor: Colors.white38,
          minimumSize: const Size(60, 44), padding: const EdgeInsets.symmetric(vertical: 2)),
      child: Column(mainAxisSize: MainAxisSize.min, children: [Icon(icon, size: 22),
        Text(label, style: const TextStyle(fontSize: 11))])),
  );
}
