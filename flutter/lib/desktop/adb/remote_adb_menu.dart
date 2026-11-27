import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';

import '../../common.dart';
import 'remote_adb_controller.dart';
import 'remote_adb_session_state.dart';

const remoteAdbActions = <String, String>{
  'pair': '配对与激活',
  'shell': 'ADB Shell',
  'paste': '远程输入',
  'accessibilityEnable': '开启本应用无障碍',
  'accessibilityDisable': '关闭本应用无障碍',
  'accessibilityPause': '暂停本应用无障碍',
  'revoke': '撤销配对',
  'status': 'ADB 状态',
  'disconnect': '停用 / 断开 ADB',
};

/// One-click operation, independent of the toolbar's mounted lifetime. No dialog,
/// package name or component is sent: the remote Android app resolves its own service.
Future<void> runRemoteAdbAccessibility(SessionID sessionId,
    RemoteAdbSessionState sessionState, String action) async {
  if (!sessionState.beginAccessibilityOperation()) return;
  final adb = RemoteAdbController(sessionId, sessionState: sessionState);
  try {
    await adb.refresh();
    if (adb.stale) {
      showToast(adb.statusError);
    } else if (adb.state['accessibilityControlSupported'] != true) {
      final reason = adb.state['accessibilityError'];
      showToast(reason is String && reason.isNotEmpty ? reason : '远端版本不支持本应用无障碍控制。');
    } else if (!adb.connected) {
      showToast(adb.activationSupported ? '请先激活手机的 ADB 能力。' : '请先连接 ADB 并验证 Shell。');
    } else {
      await adb.run(action);
      showToast(adb.message, timeout: const Duration(seconds: 5));
    }
  } finally {
    adb.dispose();
    sessionState.endAccessibilityOperation();
  }
}

/// The route owns its controller because the toolbar unmounts when auto-hidden.
Future<void> showRemoteAdbDialog(BuildContext context, SessionID sessionId, String action) {
  final sessionState = context.read<RemoteAdbSessionState?>();
  return showDialog<void>(context: context, barrierDismissible: false,
      builder: (_) => _RemoteAdbDialog(sessionId: sessionId, action: action,
          sessionState: sessionState));
}

class _RemoteAdbDialog extends StatefulWidget {
  final SessionID sessionId;
  final String action;
  final RemoteAdbSessionState? sessionState;
  const _RemoteAdbDialog({required this.sessionId, required this.action,
      this.sessionState});
  @override State<_RemoteAdbDialog> createState() => _RemoteAdbDialogState();
}

class _RemoteAdbDialogState extends State<_RemoteAdbDialog> {
  final pairPort = TextEditingController();
  final pairCode = TextEditingController();
  final connectPort = TextEditingController();
  final command = TextEditingController(text: 'id');
  final inputText = TextEditingController();
  final inputFocus = FocusNode();
  late final RemoteAdbController adb = RemoteAdbController(widget.sessionId,
      sessionState: widget.sessionState);
  String mode = 'auto';
  String validation = '';
  final offset = ValueNotifier<Offset>(Offset.zero);

  @override void initState() {
    super.initState();
    adb.refresh();
  }

  @override void dispose() {
    adb.dispose();
    pairPort.dispose(); pairCode.dispose(); connectPort.dispose(); command.dispose();
    inputText.dispose();
    inputFocus.dispose();
    offset.dispose();
    super.dispose();
  }

  int? _port(TextEditingController field) {
    if (field.text.trim().isEmpty) return 0;
    final value = int.tryParse(field.text.trim());
    if (value == null || value < 1 || value > 65535) {
      setState(() => validation = '端口必须为 1–65535，或留空自动发现。');
      return null;
    }
    return value;
  }

  Future<void> _scan() async {
    final result = await adb.run('discover');
    if (!mounted || result == null) return;
    // A fresh scan replaces stale ports, including clearing ports no longer advertised.
    pairPort.text = (result['pairPort'] ?? 0) == 0 ? '' : result['pairPort'].toString();
    connectPort.text = (result['connectPort'] ?? 0) == 0 ? '' : result['connectPort'].toString();
    setState(() => validation = '');
  }

  void _pair() {
    final port = mode == 'auto' ? 0 : _port(pairPort);
    final connection = mode == 'auto' ? 0 : _port(connectPort);
    if (port == null || connection == null) return;
    final code = pairCode.text.trim();
    if (!RegExp(r'^[0-9]{6}$').hasMatch(code)) {
      setState(() => validation = '请输入系统配对窗口中的 6 位配对码。');
      return;
    }
    pairCode.clear();
    setState(() => validation = '');
    adb.run('pairConnect', {'port': port, 'connectPort': connection, 'code': code});
  }

  Widget _portInput(TextEditingController value, String label) => TextField(
    key: ObjectKey(value), controller: value, enabled: !adb.busy,
    keyboardType: TextInputType.number, maxLength: 5,
    inputFormatters: [FilteringTextInputFormatter.digitsOnly],
    decoration: InputDecoration(labelText: label, counterText: '', border: const OutlineInputBorder()),
  );

  Widget _hint(String text) => Text(text,
      style: Theme.of(context).textTheme.bodySmall?.copyWith(
          color: Theme.of(context).colorScheme.onSurface.withOpacity(0.7),
          height: 1.5));

  Widget _detail(String label, String value) => Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
        SizedBox(width: 88, child: _hint(label)),
        const SizedBox(width: 12),
        Expanded(child: Text(value)),
      ]));

  List<Widget> _pairing() => [
    if (adb.activationSupported) ...[
      _hint(adb.reconnecting ? '恢复期间无需重新配对。'
          : adb.connected ? '服务运行期间可关闭无线调试、切换网络。'
          : '首次激活需连接 Wi-Fi 并开启无线调试。'),
      const SizedBox(height: 4),
      _hint('保持开发者选项与 USB 调试开启；重启或服务停止后需再激活。'),
      const SizedBox(height: 12),
    ],
    Text('首次配对', style: Theme.of(context).textTheme.titleSmall),
    const SizedBox(height: 6),
    _hint('手机打开“无线调试 → 使用配对码配对设备”，保持窗口打开。'),
    if (adb.activationSupported)
      _hint('请等此处显示“ADB 已激活”后再关闭 Wi-Fi 或无线调试；系统提示配对成功时，激活可能尚未完成。'),
    const SizedBox(height: 10),
    Wrap(spacing: 8, runSpacing: 8, children: [
      for (final item in const {'auto': '只填配对码', 'manual': '手动端口'}.entries)
        ChoiceChip(label: Text(item.value), selected: mode == item.key,
            onSelected: adb.busy ? null : (_) {
              FocusScope.of(context).unfocus();
              setState(() { mode = item.key; validation = ''; });
            }),
    ]),
    const SizedBox(height: 16),
    if (!adb.stale && !adb.discoverySupported)
      const Text('请更新手机端，以使用端口扫描和一键配对。'),
    if (mode == 'auto')
      _hint('自动查找端口；失败时可切换“手动端口”。')
    else ...[
      _hint('配对端口在配对码窗口，连接端口在无线调试首页；留空自动查找。'),
      const SizedBox(height: 12),
      LayoutBuilder(builder: (context, constraints) {
        final pair = _portInput(pairPort, '配对端口');
        final connect = _portInput(connectPort, '连接端口');
        return constraints.maxWidth < 420
            ? Column(children: [pair, const SizedBox(height: 12), connect])
            : Row(children: [Expanded(child: pair), const SizedBox(width: 12), Expanded(child: connect)]);
      }),
      Align(alignment: Alignment.centerRight, child: TextButton.icon(
          onPressed: adb.ready && adb.discoverySupported ? _scan : null,
          icon: const Icon(Icons.radar, size: 18), label: const Text('扫描端口'))),
    ],
    const SizedBox(height: 12),
    TextField(key: const ValueKey('pairCode'), controller: pairCode,
        enabled: !adb.busy, obscureText: true, maxLength: 6,
        autocorrect: false, enableSuggestions: false, keyboardType: TextInputType.number,
        inputFormatters: [FilteringTextInputFormatter.digitsOnly],
        onSubmitted: (_) { if (adb.ready && adb.discoverySupported && !adb.connected && !adb.reconnecting) _pair(); },
        decoration: const InputDecoration(labelText: '6 位配对码', border: OutlineInputBorder())),
    ElevatedButton.icon(onPressed: adb.ready && adb.discoverySupported && !adb.connected && !adb.reconnecting ? _pair : null,
        icon: const Icon(Icons.link), label: Text(adb.activationSupported ? '配对并激活' : '配对并连接')),
    const Divider(height: 28),
    Text('已有配对', style: Theme.of(context).textTheme.titleSmall),
    const SizedBox(height: 6),
    _hint(adb.stale ? '请刷新手机状态。'
        : adb.reconnecting ? '等待恢复后即可使用。'
        : adb.activationSupported && adb.connected ? '可直接使用 ADB，无需再次激活。'
        : adb.activationSupported && adb.paired ? '开启无线调试即可激活，无需配对码。'
        : adb.paired ? '保持无线调试开启，无需配对码。'
        : '曾配对过可尝试直接连接。'),
    const SizedBox(height: 8),
    ElevatedButton.icon(onPressed: adb.ready && !adb.reconnecting ? () {
      final port = mode == 'manual' ? _port(connectPort) : 0;
      if (port != null) adb.run('connect', {'port': port});
    } : null, icon: const Icon(Icons.check_circle_outline),
        label: Text(adb.reconnecting ? '正在恢复…' : adb.activationSupported ? adb.connected ? '验证 ADB' : '激活 ADB' : '连接 ADB')),
  ];

  List<Widget> _shell() => [
    if (!adb.connected) ...[
      _hint(adb.reconnecting ? '恢复后即可执行命令。'
          : adb.activationSupported ? '已配对时，开启无线调试即可激活。'
          : '连接已配对的 ADB 后即可执行命令。'),
      OutlinedButton(onPressed: adb.ready ? () => adb.run('connect', {'port': 0}) : null,
          child: Text(adb.activationSupported ? '激活 ADB' : '连接 ADB')),
      const SizedBox(height: 12),
    ],
    TextField(controller: command, enabled: !adb.busy, minLines: 2, maxLines: 5,
        maxLength: 4096, autocorrect: false, enableSuggestions: false,
        decoration: const InputDecoration(labelText: 'Shell 命令', hintText: '无需 adb shell 前缀', border: OutlineInputBorder())),
    ElevatedButton.icon(onPressed: adb.ready && adb.connected ? () {
      if (command.text.trim().isNotEmpty) adb.run('shell', {'command': command.text.trim()});
    } : null, icon: const Icon(Icons.play_arrow), label: const Text('执行命令')),
    const SizedBox(height: 12),
    Container(constraints: const BoxConstraints(minHeight: 120, maxHeight: 280),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(border: Border.all(color: Theme.of(context).dividerColor),
            borderRadius: BorderRadius.circular(8)),
        child: SingleChildScrollView(child: SelectableText(
            adb.output.isEmpty ? '等待命令输出…' : adb.output,
            style: const TextStyle(fontFamily: 'monospace')))),
  ];

  Future<void> _readClipboard() async {
    try {
      final data = await Clipboard.getData(Clipboard.kTextPlain);
      if (!mounted) return;
      final text = data?.text;
      if (text == null || text.isEmpty) {
        setState(() => validation = '电脑剪贴板中没有文本。');
        return;
      }
      inputText.text = text;
      setState(() => validation = '');
    } catch (_) {
      if (mounted) setState(() => validation = '无法读取电脑剪贴板，也可直接在文本框中粘贴。');
    }
  }

  Future<void> _sendText() async {
    if (!adb.ready || !adb.connected || !adb.textInputSupported) return;
    final text = inputText.text;
    final error = RemoteAdbController.validatePasteText(text);
    if (error != null) {
      setState(() => validation = error);
      return;
    }
    setState(() => validation = '');
    final result = await adb.run('paste', {'text': text});
    if (!mounted) return;
    if (result != null) inputText.clear();
    inputFocus.requestFocus();
  }

  KeyEventResult _inputKey(FocusNode node, KeyEvent event) {
    if (event.logicalKey != LogicalKeyboardKey.enter &&
        event.logicalKey != LogicalKeyboardKey.numpadEnter) {
      return KeyEventResult.ignored;
    }
    // Let the IME finish composition; Shift+Enter keeps normal multiline input.
    if (HardwareKeyboard.instance.isShiftPressed ||
        !inputText.value.composing.isCollapsed) {
      return KeyEventResult.ignored;
    }
    if (event is KeyDownEvent) _sendText();
    return KeyEventResult.handled;
  }

  List<Widget> _paste() => [
    _hint('先选中手机输入框，再发送文本；也可在远控画面按 Ctrl+V。'),
    const SizedBox(height: 4),
    _hint('Enter 发送 · Shift+Enter 换行'),
    const SizedBox(height: 12),
    if (!adb.stale && !adb.textInputSupported)
      const Text('请更新手机端，以使用远程输入。'),
    if (!adb.connected)
      OutlinedButton(onPressed: adb.ready ? () => adb.run('connect', {'port': 0}) : null,
          child: Text(adb.activationSupported ? '激活 ADB' : '连接 ADB')),
    Focus(canRequestFocus: false, onKeyEvent: _inputKey, child: TextField(
        controller: inputText, focusNode: inputFocus, autofocus: true,
        readOnly: adb.busy, minLines: 4, maxLines: 8,
        autocorrect: false, enableSuggestions: false,
        decoration: const InputDecoration(labelText: '输入或粘贴文本', border: OutlineInputBorder()))),
    const SizedBox(height: 12),
    Wrap(spacing: 12, runSpacing: 8, children: [
      OutlinedButton.icon(onPressed: adb.busy ? null : _readClipboard,
          icon: const Icon(Icons.content_paste), label: const Text('读取电脑剪贴板')),
      ElevatedButton.icon(onPressed: adb.ready && adb.connected && adb.textInputSupported ? _sendText : null,
          icon: const Icon(Icons.send), label: const Text('发送')),
    ]),
    const SizedBox(height: 12),
    _hint('会替换手机剪贴板，发送后不可撤回；目标输入框须允许粘贴。'),
  ];

  List<Widget> _content() {
    switch (widget.action) {
      case 'pair': return _pairing();
      case 'shell': return _shell();
      case 'paste': return _paste();
      case 'revoke': return [
        const Text('清除本应用配对记录，并停止所有 PC 的 ADB 操作。之后需重新配对。'),
        const SizedBox(height: 12),
        _hint('手机无线调试列表中的旧记录需手动忘记。'),
        const SizedBox(height: 16),
        ElevatedButton(onPressed: adb.ready ? () => adb.run('revoke') : null,
            child: const Text('撤销配对')),
      ];
      case 'disconnect': return [
        Text(adb.activationSupported
            ? '停用所有 PC 的 ADB，保留配对记录。下次需开启无线调试重新激活。'
            : '断开所有 PC 的 ADB，保留配对记录，可再次连接。'),
        const SizedBox(height: 8),
        _hint('仅关闭远控窗口不会停用 ADB。'),
        const SizedBox(height: 16),
        ElevatedButton(onPressed: adb.ready && (adb.connected || adb.activationSupported) ? () => adb.run('disconnect') : null,
            child: Text(adb.activationSupported ? '停用 ADB（所有 PC）' : '断开 ADB（所有 PC）')),
      ];
      default: return [
        _detail('配对记录', adb.stale ? '状态未确认' : adb.paired ? '已保存' : '无记录'),
        _detail('Shell', adb.connected ? '已验证，可用' : '未就绪'),
        _detail('上次端口', (adb.state['connectPort'] ?? 0).toString()),
        if (adb.state['keepAliveSupported'] == true) ...[
          _detail('电池优化', adb.keepAliveSummary),
          const SizedBox(height: 8),
          _hint('连接后自动申请豁免；停用 ADB 后保留，可在系统电池设置中调整。'),
        ],
        const SizedBox(height: 12),
        _hint('所有 PC 共用手机状态；有疑问时点击右上角刷新。'),
      ];
    }
  }

  @override Widget build(BuildContext context) => AnimatedBuilder(animation: adb, builder: (context, _) {
    return LayoutBuilder(builder: (context, bounds) {
      final width = min(640.0, max(0.0, bounds.maxWidth - 32));
      final height = min(widget.action == 'pair' || widget.action == 'shell' || widget.action == 'paste' ? 680.0 : 450.0,
          max(0.0, bounds.maxHeight - 32));
      final dx = max(0.0, (bounds.maxWidth - width) / 2 - 8);
      final dy = max(0.0, (bounds.maxHeight - height) / 2 - 8);
      Offset clampOffset(Offset value) => Offset(
          value.dx.clamp(-dx, dx).toDouble(), value.dy.clamp(-dy, dy).toDouble());
      return CallbackShortcuts(bindings: {
        const SingleActivator(LogicalKeyboardKey.escape): () => Navigator.pop(context),
      }, child: Center(child: ValueListenableBuilder<Offset>(valueListenable: offset,
        builder: (context, position, child) => Transform.translate(offset: clampOffset(position), child: child),
        child: RepaintBoundary(child: SizedBox(
        width: width, height: height,
        child: Material(elevation: 24, color: Theme.of(context).dialogBackgroundColor,
          borderRadius: BorderRadius.circular(16), clipBehavior: Clip.antiAlias,
          child: Column(children: [
            MouseRegion(cursor: SystemMouseCursors.move, child: GestureDetector(
              behavior: HitTestBehavior.opaque,
              onPanUpdate: (event) => offset.value = clampOffset(clampOffset(offset.value) + event.delta),
              child: Padding(padding: const EdgeInsets.fromLTRB(20, 10, 8, 10), child: Row(children: [
                const Icon(Icons.adb), const SizedBox(width: 10),
                Expanded(child: Text(remoteAdbActions[widget.action] ?? 'ADB',
                    style: Theme.of(context).textTheme.titleLarge)),
                const Icon(Icons.drag_indicator, size: 18),
                IconButton(tooltip: '关闭', onPressed: () => Navigator.pop(context), icon: const Icon(Icons.close)),
              ])),
            )),
            const Divider(height: 1),
            Expanded(child: SingleChildScrollView(padding: const EdgeInsets.all(20), child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                Row(children: [
                  Icon(adb.connected ? Icons.check_circle : Icons.info_outline,
                      color: adb.connected ? Colors.green : null, size: 20),
                  const SizedBox(width: 8),
                  Expanded(child: Text(adb.summary, style: const TextStyle(fontWeight: FontWeight.bold))),
                  IconButton(tooltip: '刷新 Android 状态', onPressed: adb.refreshing ? null : adb.refresh,
                      icon: const Icon(Icons.refresh)),
                ]),
                if (!adb.controlAllowed) _hint('只读会话，仅可查看状态。'),
                if (adb.state['busy'] == true)
                  Text((adb.state['phase'] ?? adb.state['operation'] ?? '处理中').toString()
                      + ' · 排队 ' + (adb.state['queued'] ?? 0).toString()),
                if (adb.message.isNotEmpty && adb.message != adb.summary
                    && adb.message != adb.statusError
                    && (!adb.activationSupported || adb.message != adb.state['activationError'])) ...[
                  const SizedBox(height: 8),
                  SelectableText(adb.message),
                ],
                if (adb.activationSupported && (adb.state['activationError'] ?? '').toString().isNotEmpty
                    && adb.state['activationError'] != adb.statusError) ...[
                  const SizedBox(height: 8),
                  Text(adb.state['activationError'].toString(),
                      style: TextStyle(color: Theme.of(context).colorScheme.error)),
                ],
                if (adb.statusError.isNotEmpty) ...[
                  const SizedBox(height: 8),
                  SelectableText(adb.statusError,
                    style: TextStyle(color: Theme.of(context).colorScheme.error)),
                ],
                if (validation.isNotEmpty) ...[
                  const SizedBox(height: 8),
                  Text(validation, style: TextStyle(color: Theme.of(context).colorScheme.error)),
                ],
                if (adb.busy || adb.refreshing) const Padding(
                    padding: EdgeInsets.symmetric(vertical: 8), child: LinearProgressIndicator()),
                const SizedBox(height: 16),
                ..._content(),
              ],
            ))),
            const Divider(height: 1),
            Padding(padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8), child: Row(children: [
              if (adb.busy) TextButton(onPressed: adb.cancel, child: const Text('取消请求')),
              const Spacer(),
              TextButton(onPressed: () => Navigator.pop(context), child: const Text('关闭')),
            ])),
          ]),
        ),
      )))));
    });
  });
}
