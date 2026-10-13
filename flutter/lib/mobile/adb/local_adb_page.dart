import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../pages/home_page.dart';
import 'local_adb_bridge.dart';

class LocalAdbPage extends StatefulWidget implements PageShape {
  @override
  final title = 'ADB';
  @override
  final icon = const Icon(Icons.terminal);
  @override
  final appBarActions = <Widget>[];

  LocalAdbPage({Key? key}) : super(key: key);

  @override
  State<LocalAdbPage> createState() => _LocalAdbPageState();
}

class _LocalAdbPageState extends State<LocalAdbPage>
    with AutomaticKeepAliveClientMixin, WidgetsBindingObserver {
  final _bridge = LocalAdbBridge();
  final _pairPort = TextEditingController();
  final _pairCode = TextEditingController();
  final _connectPort = TextEditingController();
  final _command = TextEditingController(text: 'id');
  LocalAdbStatus _status = const LocalAdbStatus();
  bool _loaded = false;
  bool _busy = false;
  bool _polling = false;
  bool _foreground = true;
  String _message = '正在读取 ADB 状态…';
  String _output = '';
  bool _truncated = false;
  bool _error = false;
  Timer? _timer;

  @override
  bool get wantKeepAlive => true;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _refresh(initial: true);
    // This only reads transport state; it never reconnects, pairs or starts another service.
    _timer = Timer.periodic(const Duration(seconds: 3), (_) {
      if (_foreground && !_busy) _refresh();
    });
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    _foreground = state == AppLifecycleState.resumed;
    if (_foreground) _refresh();
  }

  Future<void> _refresh({bool initial = false, bool reportLoss = true}) async {
    if (_polling) return;
    _polling = true;
    try {
      final status = await _bridge.status();
      if (!mounted) return;
      setState(() {
        final lost = _status.connected && !status.connected;
        _status = status;
        _loaded = true;
        if (initial) {
          _message = status.supported
              ? (status.connected ? '已连接本机 ADB。' : '先完成无线配对，再连接本机 ADB。')
              : '配对码无线调试需要 Android 11 或以上版本。';
        } else if (lost && !_busy && reportLoss) {
          _message = '本地 ADB 已断开。请确认无线调试开启，并使用当前连接端口重新连接。';
        }
      });
    } on PlatformException catch (e) {
      if (initial && mounted) _showError(e.message ?? '无法读取 ADB 状态。');
    } on MissingPluginException {
      if (initial && mounted) _showError('ADB 模块不可用，请使用包含新模块的 Android 安装包。');
    } finally {
      _polling = false;
    }
  }

  void _showError(String message) {
    setState(() {
      _message = message;
      _error = true;
    });
  }

  int? _port(TextEditingController controller, String name) {
    final port = int.tryParse(controller.text.trim());
    if (port == null || port < 1 || port > 65535) {
      _showError('请输入正确的$name（1–65535）。');
      return null;
    }
    return port;
  }

  Future<void> _perform(String message, Future<String> Function() action) async {
    if (_busy || _status.busy) return;
    FocusScope.of(context).unfocus();
    setState(() {
      _busy = true;
      _error = false;
      _message = message;
    });
    try {
      final message = await action();
      if (mounted) setState(() => _message = message);
    } on PlatformException catch (e) {
      if (mounted) _showError(e.message ?? 'ADB 操作失败，请重试。');
    } on MissingPluginException {
      if (mounted) _showError('ADB 模块不可用，请重新安装包含新模块的 Android 安装包。');
    } catch (_) {
      if (mounted) _showError('ADB 操作未完成，请检查状态后重试。');
    } finally {
      if (mounted) {
        setState(() => _busy = false);
        await _refresh(reportLoss: false);
      }
    }
  }

  void _pair() {
    final port = _port(_pairPort, '配对端口');
    if (port == null) return;
    final code = _pairCode.text.trim();
    if (!RegExp(r'^[0-9]{6}$').hasMatch(code)) {
      _showError('请输入系统显示的 6 位配对码。');
      return;
    }
    _pairCode.clear();
    _perform('正在配对，请保持系统配对码窗口打开…', () async {
      await _bridge.pair(port, code);
      return '配对完成。请填写无线调试首页的连接端口，然后点击“连接并验证”。';
    });
  }

  void _connect() {
    final port = _port(_connectPort, '连接端口');
    if (port == null) return;
    _perform('正在连接并验证 shell…', () async {
      final identity = await _bridge.connect(port);
      if (mounted) {
        setState(() {
          _output = identity;
          _truncated = false;
        });
      }
      return '已连接本机 ADB，shell 验证完成。';
    });
  }

  void _runCommand() {
    final command = _command.text.trim();
    if (command.isEmpty) {
      _showError('请输入 shell 命令。');
      return;
    }
    setState(() {
      _output = '';
      _truncated = false;
    });
    _perform('正在执行，最长等待 20 秒…', () async {
      final output = await _bridge.shell(command);
      if (mounted) {
        setState(() {
          _output = output.text;
          _truncated = output.truncated;
        });
      }
      return output.truncated
          ? '输出已达显示上限，命令通道已关闭。'
          : '命令结束，退出码：${output.exitCode}。';
    });
  }

  Future<void> _openSettings() async {
    try {
      await _bridge.openSettings();
    } catch (_) {
      if (mounted) _showError('请在系统设置中手动打开“开发者选项 → 无线调试”。');
    }
  }

  Future<void> _cancel() async {
    try {
      await _bridge.cancel();
    } catch (_) {
      if (mounted) _showError('无法取消操作，请等待当前操作超时。');
    }
  }

  @override
  void dispose() {
    _timer?.cancel();
    WidgetsBinding.instance.removeObserver(this);
    // The Android process owns the shared connection; this page only cancels its own work.
    if (_busy) unawaited(_bridge.cancel().catchError((Object _) {}));
    _pairPort.dispose();
    _pairCode.dispose();
    _connectPort.dispose();
    _command.dispose();
    super.dispose();
  }

  Widget _section(String title, List<Widget> children) => Card(
        margin: const EdgeInsets.only(bottom: 16),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(title, style: Theme.of(context).textTheme.titleMedium),
              const SizedBox(height: 12),
              ...children,
            ],
          ),
        ),
      );

  Widget _portField(TextEditingController controller, String label,
          {required bool enabled}) =>
      TextField(
        controller: controller,
        enabled: enabled,
        keyboardType: TextInputType.number,
        inputFormatters: [FilteringTextInputFormatter.digitsOnly],
        maxLength: 5,
        decoration: InputDecoration(labelText: label, counterText: '', border: const OutlineInputBorder()),
      );

  @override
  Widget build(BuildContext context) {
    super.build(context);
    final busy = _busy || _status.busy;
    final enabled = _loaded && _status.supported && !busy;
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Row(children: [
          const Icon(Icons.terminal),
          const SizedBox(width: 10),
          Expanded(child: Text('本机 ADB', style: Theme.of(context).textTheme.titleLarge)),
          Chip(label: Text(busy ? '处理中' : (_status.connected ? '已连接' : '未连接'))),
        ]),
        const Text('向右滑动返回主页。此页用于本机无线调试与 shell。'),
        Text(_status.paired ? '本应用已有配对记录；所有 PC 共用此记录与 ADB 连接。' : '本应用暂无配对记录。'),
        const SizedBox(height: 12),
        Text(_message, style: TextStyle(color: _error ? Theme.of(context).colorScheme.error : null)),
        if (busy) ...[
          const SizedBox(height: 8),
          const LinearProgressIndicator(),
          if (_busy) Align(alignment: Alignment.centerRight,
              child: TextButton(onPressed: _cancel, child: const Text('取消本页操作')))
          else const Text('其他客户端正在操作共享 ADB，请稍候。'),
        ],
        const SizedBox(height: 16),
        _section('1. 无线配对', [
          const Text('连接 Wi-Fi，在开发者选项中开启“无线调试”，选择“使用配对码配对设备”。'
              '\n\n请用系统分屏或小窗同时显示本页与配对码窗口；关闭配对码窗口后，原配对码和端口会失效。'),
          Align(alignment: Alignment.centerLeft,
              child: TextButton.icon(onPressed: busy ? null : _openSettings,
                  icon: const Icon(Icons.settings), label: const Text('打开开发者选项'))),
          _portField(_pairPort, '配对码窗口中的端口', enabled: enabled),
          const SizedBox(height: 12),
          TextField(
            controller: _pairCode,
            enabled: enabled,
            keyboardType: TextInputType.number,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            maxLength: 6,
            obscureText: true,
            autocorrect: false,
            enableSuggestions: false,
            decoration: const InputDecoration(labelText: '6 位配对码', counterText: '', border: OutlineInputBorder()),
          ),
          const SizedBox(height: 12),
          ElevatedButton(onPressed: enabled ? _pair : null, child: const Text('配对本机')),
        ]),
        _section('2. 连接 ADB', [
          const Text('配对后返回系统“无线调试”首页，将“IP 地址和端口”中的端口填写在这里。'
              '连接端口通常与配对端口不同；重新开启无线调试后请检查新端口。已配对的设备可直接连接。'),
          const SizedBox(height: 12),
          _portField(_connectPort, '无线调试首页中的连接端口', enabled: enabled),
          const SizedBox(height: 12),
          Wrap(spacing: 12, runSpacing: 8, children: [
            ElevatedButton(onPressed: enabled ? _connect : null, child: const Text('连接并验证')),
            OutlinedButton(onPressed: enabled && _status.connected ? () {
              _perform('正在断开…', () async {
                await _bridge.disconnect();
                return '本地 ADB 已断开，系统配对仍保留。';
              });
            } : null, child: const Text('断开')),
          ]),
        ]),
        _section('3. Shell', [
          TextField(
            controller: _command,
            enabled: enabled && _status.connected,
            minLines: 1,
            maxLines: 4,
            maxLength: 4096,
            autocorrect: false,
            enableSuggestions: false,
            decoration: const InputDecoration(labelText: 'Shell 命令', hintText: '例如：id',
                helperText: '直接填写 shell 命令，无需添加 adb shell 前缀。', border: OutlineInputBorder()),
          ),
          const SizedBox(height: 12),
          ElevatedButton(onPressed: enabled && _status.connected ? _runCommand : null,
              child: const Text('执行命令')),
          const SizedBox(height: 12),
          Container(
            constraints: const BoxConstraints(minHeight: 100, maxHeight: 320),
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(color: Theme.of(context).colorScheme.surface,
                border: Border.all(color: Theme.of(context).dividerColor), borderRadius: BorderRadius.circular(8)),
            child: SingleChildScrollView(child: SelectableText(
                _output.isEmpty ? '暂无输出' : _output,
                style: const TextStyle(fontFamily: 'monospace', fontSize: 13))),
          ),
          if (_truncated) const Padding(padding: EdgeInsets.only(top: 8), child: Text('仅显示前 64 KiB 输出。')),
          Align(alignment: Alignment.centerRight, child: TextButton(
              onPressed: _output.isEmpty ? null : () => setState(() { _output = ''; _truncated = false; }),
              child: const Text('清空输出'))),
        ]),
      ],
    );
  }
}
