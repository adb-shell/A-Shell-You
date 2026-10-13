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
                  constraints.maxHeight * 0.9 / (45 * 14)));
          final width = 70.0 * scale;
          final height = 470.0 * scale;
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
                child: FittedBox(child: SizedBox(width: 70, height: 470,
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
                          _divider(),
                          _modeButton('开投屏', Colors.blue, widget.mirror.canStart,
                              () => widget.mirror.setEnabled(true), '开启 ADB 视频；正在截图时切换为视频模式'),
                          _modeButton('关投屏', Colors.red, widget.mirror.canStop && (!widget.mirror.screenshotMode || widget.mirror.busy),
                              () => widget.mirror.setEnabled(false), '关闭所有 PC 的 ADB 视频'),
                          _divider(),
                          _modeButton('开截图', Colors.blue, widget.mirror.canScreenshot,
                              () => widget.mirror.setEnabled(true, screenshot: true),
                              widget.mirror.screenshotSupported
                                  ? 'ADB 强制连续系统截图，无需无障碍；系统保护内容仍受限制。开启后替换 ADB 视频。'
                                  : '请更新 Android 客户端以支持 ADB 连续截图'),
                          _modeButton('关截图', Colors.red, widget.mirror.canStop && (widget.mirror.screenshotMode || widget.mirror.busy),
                              () => widget.mirror.setEnabled(false), '关闭所有 PC 的 ADB 连续截图'),
                          _divider(),
                          _modeButton('开黑屏', Colors.blue, widget.mirror.canBlackOn,
                              () => widget.mirror.setBlack(true), widget.mirror.privacySupported
                                  ? '${widget.mirror.privacySummary}；独立黑色遮罩，不关闭物理屏幕，不遮挡 PC 采集画面'
                                  : '请更新 Android 客户端以支持 ADB 黑屏遮罩', privacy: true),
                          _modeButton('关黑屏', Colors.red, widget.mirror.canBlackOff,
                              () => widget.mirror.setBlack(false), '${widget.mirror.privacySummary}；任意 PC 均可关闭', privacy: true),
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

  Widget _divider() => const Divider(height: 0, thickness: 2, indent: 10, endIndent: 10, color: Colors.white54);

  Widget _modeButton(String label, Color color, bool enabled, VoidCallback action, String hint, {bool privacy = false}) => Tooltip(
    message: privacy ? hint : widget.mirror.inputError.isNotEmpty ? widget.mirror.inputError
        : widget.mirror.error.isNotEmpty ? widget.mirror.error
        : hint,
    child: ElevatedButton(onPressed: enabled ? action : null,
      style: ElevatedButton.styleFrom(backgroundColor: color, foregroundColor: Colors.white,
          disabledBackgroundColor: Colors.black26, disabledForegroundColor: Colors.white70,
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
          textStyle: const TextStyle(fontSize: 12, fontWeight: FontWeight.w500),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)), elevation: 4),
      child: Text(label)),
  );
}
