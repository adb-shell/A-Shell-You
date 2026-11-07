#!/usr/bin/env python3
"""Read-only product identity contract checks; does not build or generate code."""
import ast
import hashlib
import json
import pathlib
import re
import subprocess
import sys
import tomllib
import xml.etree.ElementTree as ET

ROOT = pathlib.Path(__file__).resolve().parents[1]
PACKAGE = "com.openworld.me"
NATIVE = "openworld"
DISPLAY = "开放世界"
checks = 0


def read(path):
    return (ROOT / path).read_text(encoding="utf-8-sig")


def require(condition, label):
    global checks
    if not condition:
        raise AssertionError(label)
    checks += 1


def reference_files():
    paths = subprocess.check_output(["git", "ls-tree", "-r", "--name-only", "HEAD"], cwd=ROOT).decode().splitlines()
    requests = "".join("HEAD:" + path + "\n" for path in paths).encode()
    data = subprocess.check_output(["git", "cat-file", "--batch"], input=requests, cwd=ROOT)
    offset, result = 0, {}
    for path in paths:
        end = data.index(b"\n", offset)
        size = int(data[offset:end].split()[-1])
        result[path] = data[end + 1:end + 1 + size]
        offset = end + size + 2
    return result


def check():
    cargo = tomllib.loads(read("Cargo.toml"))
    require(cargo["package"]["name"] == NATIVE == cargo["lib"]["name"], "Cargo package/library")
    require(cargo["package"]["version"] == "5.2.1", "Rust version unchanged")
    pubspec = read("flutter/pubspec.yaml")
    require(re.search(r"^name: flutter_hbb$", pubspec, re.M), "Flutter internal package")
    require(re.search(r"^version: 5\.2\.1\+59$", pubspec, re.M), "Flutter version unchanged")
    gradle = read("flutter/android/app/build.gradle")
    require(f'namespace "{PACKAGE}"' in gradle and f'applicationId "{PACKAGE}"' in gradle, "Android namespace/applicationId")
    android = "{http://schemas.android.com/apk/res/android}"
    for mode in ("main", "debug", "profile"):
        manifest = ET.fromstring(read(f"flutter/android/app/src/{mode}/AndroidManifest.xml"))
        require(manifest.attrib.get("package") == PACKAGE, f"{mode} manifest package")
    manifest = ET.fromstring(read("flutter/android/app/src/main/AndroidManifest.xml"))
    require(any(e.get(android + "scheme") == NATIVE for e in manifest.iter("data")), "Android deep link")
    for kind in ("activity", "service", "receiver"):
        for component in manifest.iter(kind):
            name = component.get(android + "name")
            if name and name.startswith("."):
                relative = PACKAGE.replace(".", "/") + "/" + name[1:]
                require(any((ROOT / f"flutter/android/app/src/main/{lang}/{relative}{ext}").is_file()
                            for lang, ext in (("kotlin", ".kt"), ("java", ".java"))), f"Manifest component {name}")
    resources = ET.fromstring(read("flutter/android/app/src/main/res/values/strings.xml"))
    labels = {e.get("name"): e.text for e in resources}
    require(labels["app_name"] == DISPLAY == labels["launcher_name"], "Android labels")
    require((ROOT / "flutter/android/app/src/main/res/drawable-nodpi/openworld_floating_icon.png").is_file(), "Renamed drawable exists")
    require('@drawable/openworld_floating_icon' in read("flutter/android/app/src/main/res/drawable/floating_window.xml"), "Drawable consumer")

    java_root = ROOT / "flutter/android/app/src/main/java/com/openworld/me/localadb"
    kotlin_root = ROOT / "flutter/android/app/src/main/kotlin/com/openworld/me"
    native_sources = read("src/remote_adb.rs") + read("src/remote_adb_video.rs") + read("src/ui_cm_interface.rs")
    native_exports = set(re.findall(r'fn (Java_com_openworld_me_\w+)\s*\(', native_sources))
    declarations = set()
    for file in java_root.glob("*.java"):
        source = file.read_text()
        require("package com.openworld.me.localadb;" in source, f"Java package {file.name}")
        for method in re.findall(r'\bnative\s+[\w<>\[\]]+\s+(\w+)\s*\(', source):
            declarations.add("Java_com_openworld_me_localadb_" + file.stem + "_" + method)
    for file in kotlin_root.rglob("*.kt"):
        source = file.read_text(encoding="utf-8")
        if file.name != "KeyboardKeyEventMapper.kt":
            require(source.startswith("package com.openworld.me"), f"Kotlin package {file.name}")
    for method in re.findall(r'\bexternal fun (\w+)\s*\(', read("flutter/android/app/src/main/kotlin/com/openworld/me/KeepScreenOnState.kt")):
        declarations.add("Java_com_openworld_me_KeepScreenOnState_" + method)
    require(native_exports == declarations, "ADB/keep-screen-on JNI declarations and exports match")
    proguard = read("flutter/android/app/local-adb.pro") + read("flutter/android/app/proguard-rules")
    for symbol in declarations:
        class_name = symbol.rsplit("_", 1)[0][5:].replace("_", ".")
        require(class_name in proguard, f"R8 keeps {class_name}")
    for file in java_root.glob("*.java"):
        for name in re.findall(r'app_process / (com\.openworld\.me\.localadb\.\w+)', file.read_text(encoding="utf-8")):
            require((java_root / (name.rsplit(".", 1)[1] + ".java")).is_file(), f"Shell entry {name}")
            require(name in proguard, f"R8 keeps Shell entry {name}")
    require("me.ashell" not in native_sources and "Java_me_ashell" not in native_sources, "No stale JNI package")
    channel = "com.openworld.me/local_adb/v1"
    require(channel in read("flutter/lib/mobile/adb/local_adb_bridge.dart") and channel in (java_root / "LocalAdbPlugin.java").read_text(), "ADB MethodChannel pair")
    for symbol in ("openworld_take_adb_frame", "openworld_free_adb_frame"):
        require(symbol in native_sources and symbol in read("flutter/lib/desktop/adb/adb_frame_native.dart"), f"ADB frame ABI {symbol}")
    for symbol in ("openworld_core_main", "openworld_core_main_args", "get_openworld_app_name"):
        require(symbol in read("src/flutter.rs") and symbol in read("flutter/lib/generated_bridge.dart"), f"Core ABI {symbol}")
    require('LoadLibraryA("openworld.dll")' in read("flutter/windows/runner/main.cpp"), "Windows loader")
    require("'libopenworld.so'" in read("flutter/lib/models/native_model.dart"), "Dart SO loader")
    for file in ("pkg2230.kt", "ffi.kt"):
        require('System.loadLibrary("openworld")' in read("flutter/android/app/src/main/kotlin/" + file), f"JNI loader {file}")
    require('"openworld".len()' in read("libs/portable/src/bin_reader.rs"), "Portable marker length")
    require(read("libs/portable/generate.py").count('f.write("openworld".encode') == 2, "Portable writer markers")
    proto = read("libs/hbb_common/protos/message.proto")
    require('string openworld_status = 39;' in proto, "Status wire tag/type")
    require('misc::Union::OpenworldStatus(' in read("src/client/io_loop.rs"), "Protobuf generated variant casing")
    require('OpenWorldStatus(' not in read("src/client/io_loop.rs"), "No invalid protobuf variant")
    require('"ashellSessionId": openworld_session_id' in read("src/client/helper.rs"), "Deployed broker request compatibility")
    broker = read("scripts/deploy_zego_token_service.sh")
    require('json:"openworldSessionId"' in broker and 'json:"ashellSessionId"' in broker and 'clean(req.LegacySessionId)' in broker, "Broker accepts both generations")
    for file in ("build.sh", "env.sh"):
        body = read(file)
        for suffix in ("SIGN_ENABLED", "KEYSTORE_PATH", "KEY_ALIAS", "STORE_PASSWORD", "KEY_PASSWORD"):
            require("OPENWORLD_ANDROID_" + suffix in body and "ASHELL_ANDROID_" + suffix in body, f"Signing env compatibility {file}/{suffix}")
    workflow = read(".github/workflows/Build-Clients-BT.yml")
    for token in ("Build OpenWorld Clients-BT", "libopenworld.so", "openworld.exe", "openworld.dll", "openworld-android-", "openworld-test"):
        require(token in workflow, f"CI identity {token}")
    require("ashell" not in workflow.lower(), "No old product in CI")
    require('return "com.openworld.me".to_owned()' in read("src/common.rs"), "macOS full bundle ID")
    macos = read("src/platform/macos.rs")
    require('s.replace("com.carriez.RustDesk", &full_name)' in macos, "macOS launchd template identity")
    require('Config::path("")' in macos and 'Library/Preferences/{}' in macos, "macOS template uses actual config folder")
    require('.arg(correct_app_name(&update_body))' in macos, "macOS update template identity")
    for path in ("flatpak/openworld.json",):
        require(json.loads(read(path))["id"] == PACKAGE, "Flatpak app ID")
    for path in ("build.py", "libs/portable/generate.py", "res/msi/preprocess.py"):
        ast.parse(read(path))
        require(True, f"Python syntax {path}")

    reference = reference_files()
    if b'name = "ashell"' in reference.get("Cargo.toml", b""):
        # The pre-migration HEAD permits stronger regression checks before commit.
        for path in ("Cargo.lock", "libs/portable/Cargo.lock"):
            before = tomllib.loads(reference[path].decode())
            after = tomllib.loads(read(path))
            for package in before["package"]:
                if package["name"] in ("ashell", "ashell-portable-packer"):
                    package["name"] = package["name"].replace("ashell", "openworld")
            require(before == after, f"Dependency lock versions/checksums unchanged {path}")
        before_bridge = reference["flutter/lib/generated_bridge.dart"].decode().replace("\r\n", "\n").replace("ashell", "openworld")
        require(before_bridge == read("flutter/lib/generated_bridge.dart"), "Generated bridge only changes product C symbols")
        before_proto = reference["libs/hbb_common/protos/message.proto"].decode().replace("\r\n", "\n").replace("AShell", "OpenWorld").replace("ashell_status", "openworld_status")
        require(before_proto == proto, "All protocol field numbers/types/structure unchanged")
        for path in (".info", "libs/hbb_common/src/fingerprint.rs", "libs/hbb_common/src/password_security.rs", "libs/hbb_common/protos/rendezvous.proto", "flutter/pubspec.lock", "flutter/lib/models/developer_login_bypass_model.dart", "flutter/android/app/local-adb.gradle"):
            require((ROOT / path).read_bytes().replace(b"\r\n", b"\n") == reference[path].replace(b"\r\n", b"\n"), f"Unchanged critical input {path}")
        old_icon = reference["flutter/android/app/src/main/res/drawable-nodpi/ashell_floating_icon.png"]
        new_icon = (ROOT / "flutter/android/app/src/main/res/drawable-nodpi/openworld_floating_icon.png").read_bytes()
        require(hashlib.sha256(old_icon).digest() == hashlib.sha256(new_icon).digest(), "Drawable content unchanged")
        old_workflow = reference[".github/workflows/Build-Clients-BT.yml"].decode().replace("\r\n", "\n").replace("ASHELL", "OPENWORLD").replace("AShell", "OpenWorld").replace("ashell", "openworld")
        require(old_workflow == workflow, "CI jobs/actions/permissions/toolchains unchanged")
    print(f"PASS: {checks} static identity contract checks (V0); product compilation/runtime NOT_RUN")


if __name__ == "__main__":
    try:
        check()
    except (AssertionError, KeyError, ValueError, OSError) as error:
        print(f"FAIL: {error}", file=sys.stderr)
        sys.exit(1)
