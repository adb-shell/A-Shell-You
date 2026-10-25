import 'dart:convert';

import 'package:crypto/crypto.dart';
import 'package:get/get.dart';

// This local UI gate does not replace server or endpoint authorization.
const _developerLoginPasswordHashes = <String>{
  'b4e9d3b1e3f42067e1222366438f5a09f07098b256c1a7dac7423c5db4e68fea',
  '3082bebbc61ac769512918a12e7e6c593d1c046126ae5dbada6221b056ac5055',
};

final developerLoginBypassEnabled = false.obs;

bool enableDeveloperLoginBypass(String password) {
  final passwordHash = sha256.convert(utf8.encode(password)).toString();
  if (!_developerLoginPasswordHashes.contains(passwordHash)) {
    return false;
  }
  developerLoginBypassEnabled.value = true;
  return true;
}
