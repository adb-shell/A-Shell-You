import 'dart:convert';
import 'package:flutter/material.dart';
import '../../common.dart';
import '../../models/platform_model.dart';
import '../../models/user_model.dart';
import '../../utils/http_service.dart';
import '../../utils/server_profile_state.dart';
import '../../utils/multi_window_manager.dart';

Future<void> showServerProfileSwitch(BuildContext context) async {
  if (ServerProfileState.switching.value) return;
  final raw = await bind.mainGetOption(key: 'server-profile-state');
  if (!context.mounted || raw.isEmpty) return;
  final state = jsonDecode(raw) as Map<String, dynamic>;
  final active = state['active'] as String;
  final entries = state['profiles'] as List<dynamic>;
  final target = await showDialog<String>(
    context: context,
    builder: (context) => SimpleDialog(
      title: const Text('切换服务器'),
      children: [
        ...entries.map((entry) {
          final profile = entry['profile'] as Map<String, dynamic>;
          final id = profile['id'] as String;
          final configured = entry['configured'] == true;
          return ListTile(
            leading: Icon(id == active ? Icons.radio_button_checked : Icons.radio_button_off),
            title: Text(profile['name'] as String),
            subtitle: Text(!configured ? '未配置' : id == active ? '当前使用' : '切换后需重新登录'),
            enabled: configured && id != active,
            onTap: () => Navigator.of(context).pop(id),
          );
        }),
      ],
    ),
  );
  if (target == null || !context.mounted || ServerProfileState.switching.value) return;

  ServerProfileState.switching.value = true;
  HttpService.suspended = true;
  try {
    if (rustDeskWinManager.getActiveWindows().any((id) => id != 0)) {
      throw StateError('请先结束所有远控、文件、终端和语音连接');
    }
    if (HttpService.activeRequests != 0 || HttpService.pendingProfileWrites != 0 ||
        refreshingUser || gFFI.abModel.serverProfileBusy || gFFI.groupModel.serverProfileBusy) {
      throw StateError('账号或设备信息正在同步，请稍后重试');
    }
    // Dispose idle engines so no cached session/password state crosses profiles.
    await rustDeskWinManager.closeAllSubWindows();
    if ((await rustDeskWinManager.getAllSubWindowIds()).isNotEmpty) {
      throw StateError('连接窗口尚未关闭，请稍后重试');
    }
    await bind.mainSetOption(key: 'server-profile', value: target);
    final error = await bind.mainGetOption(key: 'server-profile-error');
    if (error.isNotEmpty) throw StateError(error);
    if (ServerProfileState.activeId != target) {
      throw StateError('未确认服务器切换，请稍后重试');
    }
  } catch (error) {
    showToast(error is StateError ? error.message.toString() : '切换失败，请稍后重试');
  } finally {
    try {
      if (ServerProfileState.requiresReset) {
        await _resetServerProfileModels();
      }
    } catch (_) {
      showToast('服务器已更新，正在刷新账号和设备信息');
    } finally {
      HttpService.suspended = false;
      ServerProfileState.switching.value = false;
    }
  }
}

// A delayed IPC acknowledgement (or another main window) can update the native
// selection after the dialog completes. Block requests until UI caches follow it.
Future<void> reconcileServerProfileState() async {
  if (!ServerProfileState.requiresReset || ServerProfileState.switching.value) return;
  ServerProfileState.switching.value = true;
  HttpService.suspended = true;
  try {
    await _resetServerProfileModels();
  } catch (_) {
    // Keep requiresReset true: requests stay blocked and the next status tick retries.
  } finally {
    HttpService.suspended = false;
    ServerProfileState.switching.value = false;
  }
}

Future<void> _resetServerProfileModels() async {
  final revision = ServerProfileState.serverRevision;
  await gFFI.userModel.reset(resetOther: true);
  gFFI.peerTabModel.setMultiSelectionMode(false);
  for (final model in [gFFI.recentPeersModel, gFFI.favoritePeersModel, gFFI.lanPeersModel]) {
    model.peers = [];
    model.restPeerIds = [];
    model.notifyListeners();
  }
  await bind.mainLoadRecentPeers();
  await bind.mainLoadFavPeers();
  ServerProfileState.markApplied(revision);
  // Clear the connection form without recreating globally registered controllers.
  ServerProfileState.revision.value++;
}
