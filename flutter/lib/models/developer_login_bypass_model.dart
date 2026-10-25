import 'dart:convert';

import 'package:crypto/crypto.dart';
import 'package:get/get.dart';

// This local UI gate does not replace server or endpoint authorization.
const _developerLoginPasswordSha256 =
    'b4e9d3b1e3f42067e1222366438f5a09f07098b256c1a7dac7423c5db4e68fea';

final developerLoginBypassEnabled = false.obs;

bool enableDeveloperLoginBypass(String password) {
  if (sha256.convert(utf8.encode(password)).toString() !=
      _developerLoginPasswordSha256) {
    return false;
  }
  developerLoginBypassEnabled.value = true;
  return true;
}
