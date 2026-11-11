import 'package:get/get.dart';
import '../models/platform_model.dart';

// Main-isolate switching gate. Native session creation/IPC also enforce idleness.
class ServerProfileState {
  static final switching = false.obs;
  static final revision = 0.obs;
  static String? _appliedRevision;

  // Initialized after mainInit, before account/cache requests start.
  static void markApplied(String value) => _appliedRevision = value;
  static bool get requiresReset => _appliedRevision != null &&
      _appliedRevision != serverRevision;

  static String get activeId =>
      bind.mainGetOptionSync(key: 'server-profile');

  static String cacheKey(String peerId) => '$activeId:$peerId';
  static String get serverRevision =>
      bind.mainGetOptionSync(key: 'server-profile-revision');
}
