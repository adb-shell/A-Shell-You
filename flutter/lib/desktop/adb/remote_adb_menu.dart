import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../../common.dart';
import 'remote_adb_controller.dart';

/// Dedicated remote-session toolbar button; no dependency on video/input/terminal models.
class RemoteAdbMenu extends StatelessWidget {
  final SessionID sessionId;
  const RemoteAdbMenu({Key? key, required this.sessionId}) : super(key: key);

  @override Widget build(BuildContext context) => PopupMenuButton<String>(
    tooltip: 'ADB',
    // The toolbar unmounts its buttons when it auto-hides. The dialog, not the
    // toolbar button, owns polling and in-flight requests.
    onSelected: (action) => showDialog<void>(context: context, barrierDismissible: false,
        builder: (_) => _RemoteAdbDialog(sessionId: sessionId, initialAction: action)),
    itemBuilder: (_) => const [
      PopupMenuItem(value: 'pair', child: Text('远程 ADB 连接配对')),
      PopupMenuItem(value: 'shell', child: Text('远程 ADB 命令')),
      PopupMenuItem(value: 'revoke', child: Text('撤销 ADB 配对')),
      PopupMenuItem(value: 'status', child: Text('刷新 ADB 状态')),
      PopupMenuItem(value: 'disconnect', child: Text('断开 ADB 连接')),
    ],
    child: const Padding(padding: EdgeInsets.symmetric(horizontal: 10, vertical: 8),
        child: Text('ADB', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold))),
  );
}

class _RemoteAdbDialog extends StatefulWidget {
  final SessionID sessionId;
  final String initialAction;
  const _RemoteAdbDialog({required this.sessionId, required this.initialAction});
  @override State<_RemoteAdbDialog> createState() => _RemoteAdbDialogState();
}

class _RemoteAdbDialogState extends State<_RemoteAdbDialog> {
  final pairPort = TextEditingController();
  final pairCode = TextEditingController();
  final connectPort = TextEditingController();
  final command = TextEditingController(text: 'id');
  late String section = widget.initialAction;
  String validation = '';
  late final RemoteAdbController adb = RemoteAdbController(widget.sessionId);

  @override void initState() {
    super.initState();
    adb.watch(true);
  }

  @override void dispose() {
    adb.dispose();
    pairPort.dispose(); pairCode.dispose(); connectPort.dispose(); command.dispose();
    super.dispose();
  }

  int? _port(TextEditingController field, {bool optional = false}) {
    if (optional && field.text.trim().isEmpty) return 0;
    final port = int.tryParse(field.text.trim());
    if (port == null || port < 1 || port > 65535) {
      setState(() => validation = '端口必须为 1–65535。'); return null;
    }
    return port;
  }

  Widget _portInput(TextEditingController value, String label) => TextField(
    controller: value, enabled: !adb.busy && adb.supported,
    keyboardType: TextInputType.number, maxLength: 5,
    inputFormatters: [FilteringTextInputFormatter.digitsOnly],
    decoration: InputDecoration(labelText: label, counterText: '', border: const OutlineInputBorder()),
  );

  Future<void> _revoke() async {
    final confirmed = await showDialog<bool>(context: context, builder: (context) => AlertDialog(
      title: const Text('撤销本应用 ADB 配对'),
      content: const Text('将取消所有 PC 排队的 ADB 操作、断开共用连接并清除 Android App 的 ADB 身份和配对记录。'
          '\n系统无线调试列表中的旧条目仍需手动忘记。原远控连接继续保留。'),
      actions: [TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('返回')),
        TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('撤销'))],
    ));
    if (confirmed == true && mounted) await adb.run('revoke');
  }

  @override Widget build(BuildContext context) => AnimatedBuilder(animation: adb, builder: (context, _) {
    final ready = adb.supported && !adb.busy;
    return AlertDialog(
      title: const Text('远程 ADB · Android 共用状态'),
      content: SizedBox(width: 620, child: SingleChildScrollView(child: Column(
        mainAxisSize: MainAxisSize.min, crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(adb.summary, style: const TextStyle(fontWeight: FontWeight.bold)),
          Text('执行中：${adb.state['operation'] ?? ''}　排队：${adb.state['queued'] ?? 0}'),
          const Text('配对记录由 Android 保存，所有 PC 共用；记录不保证系统尚未撤权，连接后才验证 Shell。'),
          const SizedBox(height: 8),
          SelectableText(adb.message),
          if (validation.isNotEmpty) Text(validation, style: TextStyle(color: Theme.of(context).colorScheme.error)),
          if (adb.busy) const LinearProgressIndicator(),
          const SizedBox(height: 12),
          Wrap(spacing: 8, children: [
            for (final tab in const {'pair': '配对 / 连接', 'shell': '命令', 'status': '状态', 'revoke': '撤销'}.entries)
              ChoiceChip(label: Text(tab.value), selected: section == tab.key,
                  onSelected: (_) => setState(() { section = tab.key; validation = ''; })),
          ]),
          const SizedBox(height: 16),
          if (section == 'pair') ...[
            const Text('Android 打开“无线调试 → 使用配对码配对设备”并保持该窗口。PC 输入配对端口和六位码即可。'),
            if (adb.paired) const Padding(padding: EdgeInsets.symmetric(vertical: 8),
                child: Text('已有配对记录，可直接点击下方“我已配对，建立连接”；系统已忘记时重新配对。')),
            _portInput(pairPort, '系统配对码窗口中的配对端口'),
            const SizedBox(height: 8),
            TextField(controller: pairCode, enabled: ready, obscureText: true, maxLength: 6,
                autocorrect: false, enableSuggestions: false,
                inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                decoration: const InputDecoration(labelText: '6 位配对码', border: OutlineInputBorder())),
            ElevatedButton(onPressed: !ready ? null : () {
              final port = _port(pairPort);
              final code = pairCode.text.trim();
              if (port == null) return;
              if (!RegExp(r'^[0-9]{6}$').hasMatch(code)) {
                setState(() => validation = '请输入 6 位配对码。'); return;
              }
              pairCode.clear();
              setState(() => validation = '');
              adb.run('pair', {'port': port, 'code': code});
            }, child: const Text('让 Android 配对')),
            const Divider(),
            _portInput(connectPort, '无线调试首页的连接端口（留空自动发现）'),
            const Padding(padding: EdgeInsets.symmetric(vertical: 8), child: Text(
                '连接端口通常不同于配对端口。自动发现失败时，输入 Android 无线调试首页当前连接端口。')),
            ElevatedButton(onPressed: !ready ? null : () {
              final port = _port(connectPort, optional: true);
              if (port != null) { setState(() => validation = ''); adb.run('connect', {'port': port}); }
            }, child: const Text('我已配对，建立连接')),
          ],
          if (section == 'shell') ...[
            TextField(controller: command, enabled: ready && adb.connected, minLines: 2, maxLines: 6,
                maxLength: 4096, autocorrect: false, enableSuggestions: false,
                decoration: const InputDecoration(labelText: 'Shell 命令（不加 adb shell）', border: OutlineInputBorder())),
            ElevatedButton(onPressed: !(ready && adb.connected) ? null : () {
              if (command.text.trim().isEmpty) return;
              adb.run('shell', {'command': command.text.trim()});
            }, child: const Text('执行命令')),
            const Text('多个 PC 请求在 Android 按顺序执行；每项执行 20 秒后触发取消，输出上限 64 KiB。'),
            const SizedBox(height: 8),
            Container(constraints: const BoxConstraints(minHeight: 100, maxHeight: 280), padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(border: Border.all(color: Theme.of(context).dividerColor)),
                child: SingleChildScrollView(child: SelectableText(adb.output.isEmpty ? '暂无输出' : adb.output,
                    style: const TextStyle(fontFamily: 'monospace')))),
          ],
          if (section == 'revoke') ...[
            const Text('撤销会影响所有 PC 共用的本应用 ADB 能力，需要重新配对。不会关闭原远控会话或画面链路。'),
            TextButton(onPressed: ready ? _revoke : null, child: const Text('撤销本应用 ADB 配对…')),
          ],
          if (section == 'status' || section == 'disconnect') ...[
            Text('上次成功连接端口：${adb.state['connectPort'] ?? 0}（重新开启调试后可能变化）'),
            const Text('断开仅关闭共享 ADB，保留配对记录；以后任何已授权 PC 都可重新连接。'),
            TextButton(onPressed: ready && adb.connected ? () => adb.run('disconnect') : null,
                child: const Text('断开共用 ADB 连接')),
          ],
        ],
      ))),
      actions: [
        if (adb.busy) TextButton(onPressed: adb.cancel, child: const Text('取消本 PC 请求')),
        TextButton(onPressed: adb.refreshing ? null : adb.refresh, child: const Text('刷新状态')),
        TextButton(onPressed: () => Navigator.pop(context),
            child: const Text('关闭')),
      ],
    );
  });
}
