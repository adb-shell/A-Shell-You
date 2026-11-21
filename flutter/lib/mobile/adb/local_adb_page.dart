import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../pages/home_page.dart';
import 'local_adb_bridge.dart';

class LocalAdbPage extends StatefulWidget implements PageShape {
  @override final title = 'ADB';
  @override final icon = const Icon(Icons.adb);
  @override final appBarActions = <Widget>[];
  LocalAdbPage({Key? key}) : super(key: key);
  @override State<LocalAdbPage> createState() => _LocalAdbPageState();
}

class _LocalAdbPageState extends State<LocalAdbPage>
    with AutomaticKeepAliveClientMixin, WidgetsBindingObserver {
  final _bridge = LocalAdbBridge();
  final _pairPort = TextEditingController();
  final _pairCode = TextEditingController();
  final _connectPort = TextEditingController();
  final _command = TextEditingController(text: 'id');
  LocalAdbStatus _status = const LocalAdbStatus();
  bool _loaded = false, _busy = false, _polling = false, _foreground = true;
  bool _automatic = true, _error = false;
  bool _stale = true;
  String _message = '正在读取 ADB 状态…', _output = '';
  Timer? _timer;

  @override bool get wantKeepAlive => true;
  @override void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _refresh();
    _timer = Timer.periodic(const Duration(seconds: 3), (_) {
      if (_foreground) _refresh();
    });
  }

  @override void didChangeAppLifecycleState(AppLifecycleState state) {
    _foreground = state == AppLifecycleState.resumed;
    if (_foreground) _refresh();
  }

  Future<void> _refresh() async {
    if (_polling) return;
    _polling = true;
    try {
      final status = await _bridge.status();
      if (!mounted) return;
      setState(() {
        final changed = _status.paired != status.paired || _status.connected != status.connected;
        if (!_loaded) {
          _message = status.supported
              ? '首次开启无线调试并配对，激活内置 ADB 能力。服务存活期间无需重复连接无线调试。'
              : '无线配对需要 Android 11 或以上版本。';
          _error = false;
        } else if (!_busy && (changed || _stale) && (!_error || _stale)) {
          _message = status.activationSummary;
          _error = false;
        }
        _status = status;
        _loaded = true;
        _stale = false;
      });
    } on PlatformException catch (e) {
      if (mounted) { _stale = true; _showError(e.message ?? '无法读取 ADB 状态。'); }
    } on MissingPluginException {
      if (mounted) { _stale = true; _showError('ADB 模块不可用，请更新 Android 安装包。'); }
    } finally { _polling = false; }
  }

  void _showError(String text) => setState(() { _message = text; _error = true; });

  int? _port(TextEditingController field) {
    if (field.text.trim().isEmpty) return 0;
    final port = int.tryParse(field.text.trim());
    if (port == null || port < 1 || port > 65535) {
      _showError('端口必须为 1–65535，或留空自动发现。');
      return null;
    }
    return port;
  }

  Future<void> _perform(String message, Future<String> Function() action) async {
    if (_busy || _status.connectionBusy || _stale || !_status.supported) return;
    FocusScope.of(context).unfocus();
    setState(() { _busy = true; _error = false; _message = message; });
    try {
      final result = await action();
      if (mounted) setState(() => _message = result);
    } on PlatformException catch (e) {
      if (mounted) _showError(e.message ?? 'ADB 操作失败，请重试。');
    } on MissingPluginException {
      if (mounted) _showError('ADB 模块不可用，请更新 Android 安装包。');
    } catch (_) {
      if (mounted) _showError('ADB 操作未完成，请检查状态后重试。');
    } finally {
      if (mounted) {
        setState(() => _busy = false);
        await _refresh();
      }
    }
  }

  void _scan() => _perform('正在扫描本机配对和连接端口…', () async {
    final ports = await _bridge.discover();
    if (mounted) {
      _pairPort.text = (ports['pairPort'] ?? 0) == 0 ? '' : ports['pairPort'].toString();
      _connectPort.text = (ports['connectPort'] ?? 0) == 0 ? '' : ports['connectPort'].toString();
    }
    if (ports['ambiguous'] == true) return '发现多个本机端口，请核对系统显示的端口并手动填写。';
    if ((ports['pairPort'] ?? 0) == 0 && (ports['connectPort'] ?? 0) == 0)
      return '未发现端口。请开启无线调试，并保持系统配对窗口打开；也可手动输入端口。';
    return '已填入扫描结果，请核对后操作。系统配对窗口或无线调试开关变化后，请重新扫描。';
  });

  void _pair() {
    final port = _automatic ? 0 : _port(_pairPort);
    final connection = _automatic ? 0 : _port(_connectPort);
    if (port == null || connection == null) return;
    final code = _pairCode.text.trim();
    if (!RegExp(r'^[0-9]{6}$').hasMatch(code)) {
      _showError('请输入系统显示的 6 位配对码。'); return;
    }
    _pairCode.clear();
    _perform('正在配对，成功后将激活内置服务…', () async {
      final identity = await _bridge.pairAndConnect(port, code, connection);
      if (mounted) setState(() => _output = identity);
      return '配对成功，内置 ADB 能力已激活。PC 会自动更新状态，也可手动刷新。';
    });
  }

  void _connect() {
    final port = _automatic ? 0 : _port(_connectPort);
    if (port == null) return;
    _perform('正在激活并验证内置服务…', () async {
      final identity = await _bridge.connect(port);
      if (mounted) setState(() => _output = identity);
      return '内置 ADB 能力已激活。';
    });
  }

  void _runCommand() {
    final command = _command.text.trim();
    if (command.isEmpty) { _showError('请输入 Shell 命令。'); return; }
    setState(() => _output = '');
    _perform('正在执行命令…', () async {
      final result = await _bridge.shell(command);
      if (mounted) setState(() => _output = result.text);
      return result.truncated ? '输出超过 64 KiB，命令通道已关闭；退出码未知。'
          : '命令结束，退出码：' + result.exitCode.toString();
    });
  }

  Future<void> _openSettings() async {
    try { await _bridge.openSettings(); }
    catch (_) { if (mounted) _showError('请手动打开“开发者选项 → 无线调试”。'); }
  }

  Future<void> _cancel() async {
    try { await _bridge.cancel(); }
    catch (_) { if (mounted) _showError('取消未完成，请等待当前操作结束。'); }
  }

  @override void dispose() {
    _timer?.cancel();
    WidgetsBinding.instance.removeObserver(this);
    if (_busy) unawaited(_bridge.cancel().catchError((Object _) {}));
    _pairPort.dispose(); _pairCode.dispose(); _connectPort.dispose(); _command.dispose();
    super.dispose();
  }

  Widget _card(String title, List<Widget> children) => Card(
    margin: const EdgeInsets.only(bottom: 16),
    child: Padding(padding: const EdgeInsets.all(16), child: Column(
      crossAxisAlignment: CrossAxisAlignment.stretch, children: [
        Text(title, style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 12), ...children,
      ],
    )),
  );

  Widget _portField(TextEditingController controller, String label, bool enabled) => TextField(
    key: ObjectKey(controller), controller: controller, enabled: enabled,
    keyboardType: TextInputType.number, maxLength: 5,
    inputFormatters: [FilteringTextInputFormatter.digitsOnly],
    decoration: InputDecoration(labelText: label, counterText: '', border: const OutlineInputBorder()),
  );

  @override Widget build(BuildContext context) {
    super.build(context);
    final busy = _busy || _status.busy;
    final enabled = _loaded && !_stale && _status.supported && !_busy && !_status.connectionBusy;
    return ListView(padding: const EdgeInsets.all(16), children: [
      Row(children: [
        const Icon(Icons.adb, size: 28), const SizedBox(width: 10),
        Expanded(child: Text('ADB 能力', style: Theme.of(context).textTheme.titleLarge)),
        IconButton(tooltip: '刷新状态', onPressed: _polling ? null : _refresh, icon: const Icon(Icons.refresh)),
      ]),
      const SizedBox(height: 10),
      _card(_stale ? '状态未确认' : _status.activationSummary, [
        const Text('配对记录和激活状态由本手机管理，所有 PC 共用。本页每 3 秒查询状态；新版 PC 会接收状态变化，也可手动刷新。'),
        if (_status.connectPort != 0) Text('上次激活使用的无线端口：' + _status.connectPort.toString()),
        if (_status.keepAliveSupported) ...[
          const SizedBox(height: 8),
          Text('ADB 辅助保活：${_stale ? '状态未确认' : _status.keepAliveSummary}'),
          const Text('ADB 激活后自动为本应用设置电池优化豁免，减少息屏后的后台限制；不保证进程永不被结束。可在系统电池设置中恢复，停用 ADB 不撤销已有豁免。'),
        ],
        const SizedBox(height: 8),
        if (_status.activationError.isNotEmpty)
          Text(_status.activationError, style: TextStyle(color: Theme.of(context).colorScheme.error)),
        Text(_message, style: TextStyle(color: _error ? Theme.of(context).colorScheme.error : null)),
        if (busy) ...[
          const SizedBox(height: 8), const LinearProgressIndicator(),
          Text(_status.phase.isNotEmpty ? _status.phase
              : _status.keepAliveBusy ? '正在设置 ADB 辅助保活…' : '正在处理 ADB 请求…'),
          if (_busy) Align(alignment: Alignment.centerRight,
              child: TextButton(onPressed: _cancel, child: const Text('取消本页请求')))
          else if (!_status.keepAliveBusy) const Text('其他 PC 正在操作共享 ADB。'),
        ],
      ]),
      _card('配对与激活', [
        const Text('首次激活需要 Wi-Fi 和无线调试。激活成功后，服务存活期间可关闭无线调试、切换移动数据继续使用。'),
        const Text('请保留开发者选项和 USB 调试开启。手机重启或系统终止服务后需要重新激活；部分 ROM 的存活限制需以实际设备为准。'),
        const SizedBox(height: 12),
        const Text('首次配对：开启无线调试，选择“使用配对码配对设备”。请用分屏或小窗保持系统配对码窗口与本页同时打开。'),
        Align(alignment: Alignment.centerLeft, child: TextButton.icon(
            onPressed: _openSettings, icon: const Icon(Icons.settings), label: const Text('打开开发者选项'))),
        Wrap(spacing: 8, children: [
          ChoiceChip(label: const Text('只填配对码'), selected: _automatic,
              onSelected: busy ? null : (_) {
                FocusScope.of(context).unfocus();
                setState(() => _automatic = true);
              }),
          ChoiceChip(label: const Text('手动端口'), selected: !_automatic,
              onSelected: busy ? null : (_) {
                FocusScope.of(context).unfocus();
                setState(() => _automatic = false);
              }),
        ]),
        const SizedBox(height: 12),
        if (_automatic)
          const Text('输入 6 位配对码后点击“配对并激活 ADB”。手机会自动发现端口、配对并激活；发现失败时可切换“手动端口”。')
        else ...[
          const Text('配对端口见系统配对码窗口，连接端口见无线调试首页。填写的端口会直接使用，留空则自动发现；也可扫描填充后核对。'),
          const SizedBox(height: 12),
          _portField(_pairPort, '配对端口（留空自动）', !busy),
          const SizedBox(height: 12),
          _portField(_connectPort, '连接端口（留空自动）', !busy),
          Align(alignment: Alignment.centerRight, child: TextButton.icon(
              onPressed: enabled ? _scan : null, icon: const Icon(Icons.radar, size: 18),
              label: const Text('扫描并填充端口'))),
        ],
        const SizedBox(height: 12),
        TextField(key: const ValueKey('pairCode'), controller: _pairCode,
            enabled: !busy, keyboardType: TextInputType.number,
            obscureText: true, maxLength: 6, autocorrect: false, enableSuggestions: false,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            onSubmitted: (_) { if (enabled && !_status.connected) _pair(); },
            decoration: const InputDecoration(labelText: '6 位配对码', border: OutlineInputBorder())),
        ElevatedButton.icon(onPressed: enabled && !_status.connected ? _pair : null, icon: const Icon(Icons.link),
            label: const Text('配对并激活 ADB')),
        const SizedBox(height: 8),
        const Text('能力可用时无需再配对。服务停止后，开启无线调试并点击下方激活；已有配对身份无需重新输入配对码。'),
        OutlinedButton(onPressed: enabled ? _connect : null, child: Text(_status.connected ? '复用已激活能力' : '我已配对，激活 ADB 能力')),
      ]),
      _card('ADB Shell', [
        TextField(controller: _command, enabled: !busy, minLines: 2, maxLines: 5,
            maxLength: 4096, autocorrect: false, enableSuggestions: false,
            decoration: const InputDecoration(labelText: 'Shell 命令（不加 adb shell）', border: OutlineInputBorder())),
        ElevatedButton.icon(onPressed: enabled && _status.connected ? _runCommand : null,
            icon: const Icon(Icons.play_arrow), label: const Text('执行命令')),
        const Text('最长执行 20 秒；输出最多 64 KiB。'),
        const SizedBox(height: 10),
        Container(padding: const EdgeInsets.all(12),
            constraints: const BoxConstraints(minHeight: 90, maxHeight: 300),
            decoration: BoxDecoration(border: Border.all(color: Theme.of(context).dividerColor),
                borderRadius: BorderRadius.circular(8)),
            child: SingleChildScrollView(child: SelectableText(_output.isEmpty ? '等待输出…' : _output,
                style: const TextStyle(fontFamily: 'monospace')))),
        TextButton(onPressed: enabled && (_status.connected || _status.activationState != 'inactive') ? () => _perform('正在停用设备 ADB 能力…', () async {
          await _bridge.disconnect(); return 'ADB 能力已停用，配对记录保留；下次需要重新激活。';
        }) : null, child: const Text('停用设备 ADB 能力（所有 PC）')),
      ]),
    ]);
  }
}
