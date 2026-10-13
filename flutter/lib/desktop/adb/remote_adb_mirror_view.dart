import 'dart:math' as math;
import 'dart:typed_data';
import 'dart:ui' as ui;
import 'package:flutter/material.dart';
import 'remote_adb_mirror.dart';

/// A separate, draggable viewport. Pointer events stop here, never at the legacy input canvas.
class RemoteAdbMirrorView extends StatefulWidget {
  const RemoteAdbMirrorView({super.key, required this.mirror});
  final RemoteAdbMirror mirror;
  @override
  State<RemoteAdbMirrorView> createState() => _RemoteAdbMirrorViewState();
}

class _RemoteAdbMirrorViewState extends State<RemoteAdbMirrorView> {
  Offset? _position;
  bool _large = false;
  @override
  Widget build(BuildContext context) => AnimatedBuilder(
    animation: widget.mirror,
    builder: (context, _) {
      final mirror = widget.mirror;
      if (!mirror.active) return const SizedBox.shrink();
      return LayoutBuilder(builder: (context, constraints) {
        final width = math.min(constraints.maxWidth, _large ? 1000.0 : 480.0);
        final height = math.min(constraints.maxHeight, _large ? 800.0 : 650.0);
        Offset clamp(Offset p) => Offset(
          p.dx.clamp(0.0, math.max(0.0, constraints.maxWidth - width)).toDouble(),
          p.dy.clamp(0.0, math.max(0.0, constraints.maxHeight - height)).toDouble());
        final position = clamp(_position ?? Offset((constraints.maxWidth - width) / 2, 48));
        return Stack(children: [Positioned(left: position.dx, top: position.dy, width: width, height: height,
          child: Listener(behavior: HitTestBehavior.opaque,
            child: GestureDetector(behavior: HitTestBehavior.opaque,
              onTap: () {}, onPanUpdate: (_) {},
              child: Material(elevation: 10, color: Colors.black, borderRadius: BorderRadius.circular(10),
                clipBehavior: Clip.antiAlias,
                child: Column(children: [
                  GestureDetector(behavior: HitTestBehavior.opaque,
                    onPanUpdate: (event) => setState(() => _position = clamp(position + event.delta)),
                    child: Container(color: Theme.of(context).colorScheme.surface,
                      child: Row(children: [
                        const SizedBox(width: 12), const Icon(Icons.adb, size: 18),
                        const SizedBox(width: 8), const Expanded(child: Text('ADB 投屏')),
                        IconButton(tooltip: _large ? '缩小画面' : '放大画面',
                          onPressed: () => setState(() => _large = !_large), icon: Icon(_large ? Icons.fullscreen_exit : Icons.fullscreen)),
                        IconButton(tooltip: '关闭所有 PC 的 ADB 投屏', onPressed: mirror.canStop ? () => mirror.setEnabled(false) : null,
                          icon: const Icon(Icons.stop_screen_share_outlined)),
                      ]))),
                  Expanded(child: mirror.image == null
                      ? Center(child: Text(mirror.summary, style: const TextStyle(color: Colors.white70), textAlign: TextAlign.center))
                      : _AdbFrame(bytes: mirror.image!, mirror: mirror)),
                  Padding(padding: const EdgeInsets.all(8), child: Text(
                    mirror.inputError.isNotEmpty ? mirror.inputError : mirror.error.isEmpty
                        ? (mirror.touchReady ? 'ADB 触摸优先 · 可点击、拖动；关投屏对所有 PC 生效。' : '正在准备 ADB 触摸…') : mirror.error,
                    textAlign: TextAlign.center, style: const TextStyle(color: Colors.white70, fontSize: 11))),
                ])),
            ),
          ),
        )]);
      });
    });
}

/// At most one decode and one displayed image; bypass Flutter's still-image cache.
class _AdbFrame extends StatefulWidget {
  const _AdbFrame({required this.bytes, required this.mirror});
  final Uint8List bytes;
  final RemoteAdbMirror mirror;
  @override
  State<_AdbFrame> createState() => _AdbFrameState();
}

class _AdbFrameState extends State<_AdbFrame> {
  ui.Image? _image;
  bool _decoding = false;
  bool _failed = false;
  int? _pointer;
  Offset _last = Offset.zero;
  Size _box = Size.zero;
  DateTime _lastMove = DateTime.fromMillisecondsSinceEpoch(0);

  void _touch(int action, Offset local) {
    final image = _image;
    if (image == null || _box.isEmpty) return;
    final scale = math.min(_box.width / image.width, _box.height / image.height);
    final left = (_box.width - image.width * scale) / 2;
    final top = (_box.height - image.height * scale) / 2;
    final x = ((local.dx - left) / scale).round().clamp(0, image.width - 1).toInt();
    final y = ((local.dy - top) / scale).round().clamp(0, image.height - 1).toInt();
    widget.mirror.touch(action, x, y, image.width, image.height);
  }

  @override
  void initState() { super.initState(); _decode(); }

  @override
  void didUpdateWidget(covariant _AdbFrame oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (!identical(oldWidget.bytes, widget.bytes)) _decode();
  }

  Future<void> _decode() async {
    if (_decoding || !mounted) return;
    _decoding = true;
    final bytes = widget.bytes;
    ui.Codec? codec;
    try {
      codec = await ui.instantiateImageCodec(bytes);
      final frame = await codec.getNextFrame();
      if (!mounted) { frame.image.dispose(); return; }
      final previous = _image;
      setState(() { _image = frame.image; _failed = false; });
      WidgetsBinding.instance.addPostFrameCallback((_) => previous?.dispose());
    } catch (_) {
      if (mounted) setState(() => _failed = true);
    } finally {
      codec?.dispose();
      _decoding = false;
      if (mounted && !identical(bytes, widget.bytes)) _decode();
    }
  }

  @override
  Widget build(BuildContext context) => _failed
      ? const Center(child: Text('ADB 画面解析失败', style: TextStyle(color: Colors.white70)))
      : LayoutBuilder(builder: (context, constraints) {
        _box = constraints.biggest;
        return Listener(behavior: HitTestBehavior.opaque,
          onPointerDown: (event) {
            if (_pointer != null || !widget.mirror.touchReady || widget.mirror.ffi.ffiModel.viewOnly) return;
            final image = _image;
            if (image == null) return;
            final scale = math.min(_box.width / image.width, _box.height / image.height);
            final rect = Rect.fromCenter(center: Offset(_box.width / 2, _box.height / 2),
                width: image.width * scale, height: image.height * scale);
            if (!rect.contains(event.localPosition)) return;
            _pointer = event.pointer; _last = event.localPosition;
            _touch(0, _last);
          },
          onPointerMove: (event) {
            if (_pointer != event.pointer) return;
            _last = event.localPosition;
            if (DateTime.now().difference(_lastMove).inMilliseconds >= 16) {
              _lastMove = DateTime.now(); _touch(2, _last);
            }
          },
          onPointerUp: (event) {
            if (_pointer != event.pointer) return;
            _touch(1, event.localPosition); _pointer = null;
          },
          onPointerCancel: (event) {
            if (_pointer != event.pointer) return;
            _touch(1, _last); _pointer = null;
          },
          child: SizedBox.expand(child: RawImage(image: _image, fit: BoxFit.contain, filterQuality: FilterQuality.low)));
      });

  @override
  void dispose() {
    if (_pointer != null) _touch(1, _last);
    _image?.dispose(); _image = null; super.dispose();
  }
}
