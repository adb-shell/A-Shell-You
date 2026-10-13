import 'dart:math' as math;
import 'dart:typed_data';
import 'dart:ui' as ui;
import 'package:flutter/material.dart';
import '../../consts.dart';
import 'remote_adb_mirror.dart';

/// Inline ADB surface. Its pointer events never enter the ordinary image canvas.
class RemoteAdbMirrorView extends StatelessWidget {
  const RemoteAdbMirrorView({super.key, required this.mirror, this.alignment = Alignment.center});
  final RemoteAdbMirror mirror;
  final Alignment alignment;
  @override
  Widget build(BuildContext context) => AnimatedBuilder(
    animation: mirror,
    builder: (context, _) {
      final error = mirror.error.isNotEmpty ? mirror.error : mirror.inputError;
      return ColoredBox(color: kColorCanvas, child: Listener(behavior: HitTestBehavior.opaque,
        child: GestureDetector(behavior: HitTestBehavior.opaque, onTap: () {}, onPanUpdate: (_) {},
          child: Stack(fit: StackFit.expand, children: [
            if (mirror.image != null) _AdbFrame(bytes: mirror.image!, mirror: mirror, alignment: alignment)
            else Center(child: Padding(padding: const EdgeInsets.all(24),
              child: Text(mirror.summary, textAlign: TextAlign.center, style: const TextStyle(color: Colors.white70)))),
            if (mirror.image != null) Positioned(left: 12, right: 12, bottom: 12,
              child: IgnorePointer(child: Center(child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                decoration: BoxDecoration(color: Colors.black54, borderRadius: BorderRadius.circular(6)),
                child: Text(error.isNotEmpty ? error : mirror.screenshotMode ? 'ADB 连续截图' : 'ADB 投屏',
                  textAlign: TextAlign.center, style: const TextStyle(color: Colors.white70, fontSize: 11)),
              )))),
          ]))));
    });
}

/// At most one decode and one displayed image; bypass Flutter's still-image cache.
class _AdbFrame extends StatefulWidget {
  const _AdbFrame({required this.bytes, required this.mirror, required this.alignment});
  final Uint8List bytes;
  final RemoteAdbMirror mirror;
  final Alignment alignment;
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
    final left = (_box.width - image.width * scale) * (widget.alignment.x + 1) / 2;
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
            final rect = widget.alignment.inscribe(Size(image.width * scale, image.height * scale), Offset.zero & _box);
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
          child: SizedBox.expand(child: RawImage(image: _image, fit: BoxFit.contain,
              alignment: widget.alignment, filterQuality: FilterQuality.low)));
      });

  @override
  void dispose() {
    if (_pointer != null) _touch(1, _last);
    _image?.dispose(); _image = null; super.dispose();
  }
}
