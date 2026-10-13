//! Device-owned scrcpy stream. This module never publishes product VideoFrame or RGBA events.
use hbb_common::message_proto::{Message, Misc, RemoteAdbVideo};

const MAX_FRAME: usize = 2 * 1024 * 1024 + 65536;

fn message(packet: RemoteAdbVideo) -> Message {
    let mut misc = Misc::new();
    misc.set_remote_adb_video(packet);
    let mut msg = Message::new();
    msg.set_misc(misc);
    msg
}

#[cfg(not(target_os = "ios"))]
pub fn dispatch(owner: i32, normal: bool, control: bool, packet: RemoteAdbVideo, tx: crate::server::Sender) {
    // Dispatch is reached only after normal peer authentication. No payload becomes shell text.
    if packet.token.is_empty() || packet.token.len() > 64
        || (packet.action != "touch" && !packet.data.is_empty()) || packet.data.len() > 20
        || packet.action.len() > 16 || !packet.error.is_empty() || !packet.state.is_empty()
        || !packet.input_error.is_empty() || !packet.format.is_empty()
        || !packet.privacy_state.is_empty() || !packet.privacy_error.is_empty() { return; }
    if !normal || (!control && matches!(packet.action.as_str(), "start" | "screenshot" | "layout" | "stop" | "touch" | "black_on" | "black_off")) { return; }
    #[cfg(target_os = "android")]
    android::dispatch(owner, packet, tx);
    #[cfg(not(target_os = "android"))]
    {
        let _ = owner;
        if packet.action != "watch" { return; }
        let response = RemoteAdbVideo { token: packet.token, action: "state".into(),
            state: "error".into(), error: "远端不支持 Android ADB 投屏。".into(), ..Default::default() };
        let _ = tx.send((hbb_common::tokio::time::Instant::now(), std::sync::Arc::new(message(response))));
    }
}

#[cfg(target_os = "android")]
pub mod android {
    use super::*;
    use crate::server::Sender;
    use jni::{JNIEnv, JavaVM, objects::{GlobalRef, JByteArray, JClass, JObject, JString, JValue}, sys::{jboolean, jint, jlong}};
    use std::{collections::{HashMap, VecDeque}, sync::{Arc, Mutex}};

    struct Bridge { vm: JavaVM, manager: GlobalRef }
    struct Viewer { token: String, tx: Sender, pending: VecDeque<u64>, need_key: bool, screenshots: bool, layouts: bool }
    struct Hub { viewers: HashMap<i32, Viewer>, state: RemoteAdbVideo, config: Vec<u8>, sequence: u64 }
    lazy_static::lazy_static! {
        static ref BRIDGE: Mutex<Option<Bridge>> = Mutex::new(None);
        static ref HUB: Mutex<Hub> = Mutex::new(Hub { viewers: HashMap::new(), config: Vec::new(), sequence: 0,
            state: RemoteAdbVideo { action: "state".into(), state: "idle".into(), supported: true,
                format: "h264".into(), screenshot_supported: true, layout_supported: true,
                privacy_supported: true, privacy_state: "idle".into(), ..Default::default() } });
    }

    #[no_mangle]
    pub extern "system" fn Java_me_ashell_localadb_LocalAdbMirror_register(env: JNIEnv, _class: JClass, manager: JObject) {
        if let (Ok(vm), Ok(manager)) = (env.get_java_vm(), env.new_global_ref(manager)) {
            if let Ok(mut bridge) = BRIDGE.lock() { *bridge = Some(Bridge { vm, manager }); }
        }
    }

    // Never hold HUB while crossing JNI: Java may synchronously publish state back to HUB.
    fn invoke(action: &str) -> bool {
        let Ok(bridge) = BRIDGE.lock() else { return false; };
        let Some(bridge) = bridge.as_ref() else { return false; };
        let Ok(mut env) = bridge.vm.attach_current_thread() else { return false; };
        let result = env.with_local_frame(8, |env| -> jni::errors::Result<()> {
            let action: JObject = env.new_string(action)?.into();
            env.call_method(bridge.manager.as_obj(), "controlMirror", "(Ljava/lang/String;)V", &[JValue::Object(&action)])?;
            Ok(())
        });
        if result.is_err() { let _ = env.exception_clear(); }
        result.is_ok()
    }

    fn send(viewer: &Viewer, mut packet: RemoteAdbVideo) -> bool {
        if packet.format == "layout" && !viewer.layouts {
            if packet.action == "frame" { return false; }
            packet.format = "h264".into(); // old PCs reject unknown formats before reading state
            if matches!(packet.state.as_str(), "starting" | "streaming" | "stopping") {
                packet.state = "error".into(); packet.error = "远端正在使用 ADB 穿透，请更新 PC 客户端。".into();
            }
        }
        if packet.format == "jpeg" && !viewer.screenshots {
            if packet.action == "frame" { return false; }
            if matches!(packet.state.as_str(), "starting" | "streaming" | "stopping") {
                packet.state = "error".into();
                packet.error = "远端正在使用 ADB 截图传输，请更新 PC 客户端。".into();
            }
        }
        packet.token = viewer.token.clone();
        viewer.tx.send((hbb_common::tokio::time::Instant::now(), Arc::new(message(packet)))).is_ok()
    }

    pub fn dispatch(owner: i32, packet: RemoteAdbVideo, tx: Sender) {
        let action = packet.action.as_str();
        let Ok(mut hub) = HUB.lock() else { return; };
        if action == "watch" {
            if hub.viewers.len() >= 32 && !hub.viewers.contains_key(&owner) { return; }
            let snapshot = hub.state.clone();
            let viewer = hub.viewers.entry(owner).or_insert_with(|| Viewer {
                token: packet.token.clone(), tx: tx.clone(), pending: VecDeque::new(), need_key: true, screenshots: packet.screenshot_supported, layouts: packet.layout_supported });
            if viewer.token != packet.token {
                *viewer = Viewer { token: packet.token, tx, pending: VecDeque::new(), need_key: true, screenshots: packet.screenshot_supported, layouts: packet.layout_supported };
            }
            send(viewer, snapshot);
            return;
        }
        let Some(viewer) = hub.viewers.get_mut(&owner) else { return; };
        if viewer.token != packet.token { return; }
        if action == "screenshot" && !viewer.screenshots { return; }
        if action == "layout" && !viewer.layouts { return; }
        if action == "touch" {
            if packet.data.len() != 20 { return; }
            let values: Vec<i32> = packet.data.chunks_exact(4)
                .map(|b| i32::from_be_bytes([b[0], b[1], b[2], b[3]])).collect();
            if !(0..=2).contains(&values[0]) || !(1..=16384).contains(&values[3])
                || !(1..=16384).contains(&values[4]) { return; }
            drop(hub);
            invoke_ints("injectMirrorTouch", "(IIIIII)V", &[owner, values[0], values[1], values[2], values[3], values[4]]);
            return;
        }
        if action == "ack" || action == "nack" {
            if let Some(index) = viewer.pending.iter().position(|seq| *seq == packet.sequence) {
                viewer.pending.remove(index);
                if action == "nack" { viewer.need_key = true; }
            }
            return;
        }
        let stop = if action == "unwatch" {
            hub.viewers.remove(&owner);
            hub.viewers.is_empty()
        } else { action == "stop" };
        drop(hub);
        if action == "unwatch" { release_touch(owner); }
        if !matches!(action, "start" | "screenshot" | "layout" | "black_on" | "black_off") && !stop { return; }
        if !invoke(if action == "unwatch" { "stop_all" } else { action }) {
            publish(0, "error".into(), "Android 投屏模块尚未初始化，请更新两端客户端。".into(), "h264".into());
        }
    }

    pub fn disconnect(owner: i32) {
        release_touch(owner);
        let stop = HUB.lock().map(|mut hub| hub.viewers.remove(&owner).is_some() && hub.viewers.is_empty()).unwrap_or(false);
        if stop { invoke("stop_all"); }
    }

    fn publish(generation: u64, state: String, error: String, format: String) {
        if let Ok(mut hub) = HUB.lock() {
            let new_generation = generation != hub.state.generation;
            hub.state = RemoteAdbVideo { action: "state".into(), generation, state, error, supported: true,
                adb_priority: hub.state.adb_priority, touch_ready: hub.state.touch_ready,
                format, screenshot_supported: true, layout_supported: true,
                input_error: hub.state.input_error.clone(), privacy_supported: true,
                privacy_state: hub.state.privacy_state.clone(), privacy_error: hub.state.privacy_error.clone(), ..Default::default() };
            if hub.state.state != "streaming" { hub.config.clear(); }
            let snapshot = hub.state.clone();
            for viewer in hub.viewers.values_mut() {
                if new_generation { viewer.pending.clear(); }
                if snapshot.state != "streaming" { viewer.need_key = true; }
                send(viewer, snapshot.clone());
            }
        }
    }

    fn invoke_ints(method: &str, signature: &str, values: &[i32]) {
        let Ok(bridge) = BRIDGE.lock() else { return; };
        let Some(bridge) = bridge.as_ref() else { return; };
        let Ok(mut env) = bridge.vm.attach_current_thread() else { return; };
        let args: Vec<JValue> = values.iter().map(|v| JValue::Int(*v)).collect();
        if env.call_method(bridge.manager.as_obj(), method, signature, &args).is_err() { let _ = env.exception_clear(); }
    }

    pub fn release_touch(owner: i32) { invoke_ints("releaseTouchOwner", "(I)V", &[owner]); }

    /// True consumes this pointer event and prevents any Accessibility injection.
    pub fn route_touch(owner: i32, kind: i32, mask: i32, x: i32, y: i32, allowed: bool) -> bool {
        if kind != 0 && !matches!(mask, 0 | 8 | 16 | 9 | 10 | 17 | 18 | 3 | 4) { return false; }
        let Ok(bridge) = BRIDGE.lock() else { return false; };
        let Some(bridge) = bridge.as_ref() else { return false; };
        let Ok(mut env) = bridge.vm.attach_current_thread() else { return false; };
        let result = env.call_method(bridge.manager.as_obj(), "routeAdbTouch", "(IIIIIZ)Z", &[
            JValue::Int(owner), JValue::Int(kind), JValue::Int(mask), JValue::Int(x), JValue::Int(y), JValue::Bool(allowed as u8)]);
        match result { Ok(value) => value.z().unwrap_or(true), Err(_) => { let _ = env.exception_clear(); true } }
    }

    #[no_mangle]
    pub extern "system" fn Java_me_ashell_localadb_LocalAdbTouch_status(mut env: JNIEnv, _class: JClass,
        priority: jboolean, ready: jboolean, error: JString) {
        let Ok(error) = env.get_string(&error).map(|s| s.to_string_lossy().into_owned()) else { return; };
        if error.len() > 2048 { return; }
        if let Ok(mut hub) = HUB.lock() {
            hub.state.adb_priority = priority != 0;
            hub.state.touch_ready = ready != 0;
            hub.state.input_error = error;
            let snapshot = hub.state.clone();
            for viewer in hub.viewers.values() { send(viewer, snapshot.clone()); }
        }
    }

    #[no_mangle]
    pub extern "system" fn Java_me_ashell_localadb_LocalAdbPrivacy_state(mut env: JNIEnv, _class: JClass,
        state: JString, error: JString) {
        let Ok(state) = env.get_string(&state).map(|s| s.to_string_lossy().into_owned()) else { return; };
        let Ok(error) = env.get_string(&error).map(|s| s.to_string_lossy().into_owned()) else { return; };
        if state.len() > 16 || error.len() > 2048 { return; }
        if let Ok(mut hub) = HUB.lock() {
            hub.state.privacy_state = state;
            hub.state.privacy_error = error;
            let snapshot = hub.state.clone();
            for viewer in hub.viewers.values() { send(viewer, snapshot.clone()); }
        }
    }

    #[no_mangle]
    pub extern "system" fn Java_me_ashell_localadb_AdbCaptureProfile_quality(env: JNIEnv, _class: JClass,
        width: jint, height: jint) -> jni::sys::jintArray {
        if width <= 0 || height <= 0 || width > 1280 || height > 1280 { return std::ptr::null_mut(); }
        let (quality, fps) = crate::server::video_service::VIDEO_QOS.lock()
            .map(|q| (q.latest_quality(), q.fps().clamp(1, 30)))
            .unwrap_or((scrap::codec::Quality::Balanced, 15));
        let ratio = quality.ratio().clamp(0.1, 2.0);
        // Same selected quality, with a bounded budget for this additional independent stream.
        let bitrate = ((width * height) as f32 * 2.0 * ratio / scrap::codec::BR_BALANCED) as i32;
        let jpeg = match quality { scrap::codec::Quality::Low => 60, scrap::codec::Quality::Best => 85,
            scrap::codec::Quality::Custom(_) => (60.0 + ratio * 20.0).round().clamp(50.0, 90.0) as i32, _ => 75 };
        let Ok(array) = env.new_int_array(3) else { return std::ptr::null_mut(); };
        if env.set_int_array_region(&array, 0, &[bitrate.clamp(200000, 1000000), fps as i32, jpeg]).is_err() {
            return std::ptr::null_mut();
        }
        array.into_raw()
    }

    #[no_mangle]
    pub extern "system" fn Java_me_ashell_localadb_LocalAdbMirror_state(mut env: JNIEnv, _class: JClass,
        generation: jlong, state: JString, error: JString, format: JString) {
        let Ok(state) = env.get_string(&state).map(|s| s.to_string_lossy().into_owned()) else { return; };
        let Ok(error) = env.get_string(&error).map(|s| s.to_string_lossy().into_owned()) else { return; };
        let Ok(format) = env.get_string(&format).map(|s| s.to_string_lossy().into_owned()) else { return; };
        if generation >= 0 && state.len() <= 16 && error.len() <= 2048 && matches!(format.as_str(), "h264" | "jpeg" | "layout") {
            publish(generation as u64, state, error, format);
        }
    }

    #[no_mangle]
    pub extern "system" fn Java_me_ashell_localadb_LocalAdbMirror_frame(env: JNIEnv, _class: JClass,
        generation: jlong, width: jint, height: jint, config: jboolean, key: jboolean, data: JByteArray, screenshot: jboolean,
        source_width: jint, source_height: jint) {
        if width <= 0 || height <= 0 || width > 1280 || height > 1280 { return; }
        if source_width <= 0 || source_height <= 0 || source_width > 16384 || source_height > 16384 { return; }
        let Ok(size) = env.get_array_length(&data) else { return; };
        if size <= 0 || size as usize > MAX_FRAME || (config != 0 && size > 65536) { return; }
        let Ok(data) = env.convert_byte_array(data) else { return; };
        let Ok(mut hub) = HUB.lock() else { return; };
        if hub.state.generation != generation as u64 || (hub.state.format != "h264") != (screenshot != 0) { return; }
        if config != 0 {
            hub.config = data;
            for viewer in hub.viewers.values_mut() { viewer.need_key = true; }
            return;
        }
        if hub.state.state != "streaming" { return; }
        hub.sequence = hub.sequence.wrapping_add(1);
        let sequence = hub.sequence;
        let keyframe = key != 0;
        let mut payload = if keyframe { hub.config.clone() } else { Vec::new() };
        payload.extend(data);
        if payload.len() > MAX_FRAME { return; }
        let packet = RemoteAdbVideo { action: "frame".into(), generation: generation as u64, sequence,
            width: width as u32, height: height as u32, keyframe, data: payload.into(),
            format: hub.state.format.clone(), source_width: source_width as u32, source_height: source_height as u32, ..Default::default() };
        for viewer in hub.viewers.values_mut() {
            if viewer.pending.len() >= 4 { viewer.need_key = true; continue; }
            if viewer.need_key && !keyframe { continue; }
            if send(viewer, packet.clone()) {
                viewer.pending.push_back(sequence);
                viewer.need_key = false;
            }
        }
    }
}

#[cfg(all(feature = "flutter", not(any(target_os = "android", target_os = "ios"))))]
pub mod pc {
    use super::*;
    use crate::{client::{Data, Interface}, flutter::sessions};
    use hbb_common::{base64::{engine::general_purpose::STANDARD, Engine}, message_proto::{video_frame, EncodedVideoFrame, EncodedVideoFrames}};
    use scrap::{codec::Decoder, CodecFormat, ImageFormat, ImageRgb, ImageTexture};
    use serde_json::{json, Value};
    use image::ImageDecoder;
    use std::{collections::HashMap, sync::{Arc, Mutex, mpsc::{sync_channel, SyncSender}, atomic::{AtomicBool, Ordering}}, time::{Duration, Instant}};
    use uuid::Uuid;

    struct View { generation: u64, state: String, error: String, image: Option<String>, supported: bool,
        adb_priority: bool, touch_ready: bool, input_error: String, format: String, screenshot_supported: bool,
        privacy_supported: bool, privacy_state: String, privacy_error: String,
        layout_supported: bool, source_width: u32, source_height: u32 }
    struct Entry { peer: String, token: String, queue: SyncSender<RemoteAdbVideo>, view: Arc<Mutex<View>>,
        resync: Arc<AtomicBool>, live: Arc<AtomicBool>, since: Instant, touch_sequence: u64 }
    lazy_static::lazy_static! { static ref ENTRIES: Mutex<HashMap<Uuid, Entry>> = Mutex::new(HashMap::new()); }

    fn send(session_id: Uuid, packet: RemoteAdbVideo) -> bool {
        let Some(session) = sessions::get_session_by_session_id(&session_id) else { return false; };
        let Ok(sender) = session.sender.read() else { return false; };
        sender.as_ref().map(|s| s.send(Data::Message(message(packet))).is_ok()).unwrap_or(false)
    }

    pub fn controller(session_id: Uuid, param: &str) -> Option<String> {
        if param.len() > 256 { return None; }
        let request: Value = serde_json::from_str(param).ok()?;
        let action = request["action"].as_str()?;
        let owner = request["owner"].as_str()?;
        if owner.len() != 36 || Uuid::parse_str(owner).is_err() { return None; }
        let mut entries = ENTRIES.lock().ok()?;
        if action == "watch" {
            if let Some(entry) = entries.get(&session_id) {
                if entry.token == owner { return Some("{}".into()); }
            }
            // Old page teardown must not remove a new subscription with the same session UUID.
            if let Some(entry) = entries.remove(&session_id) { entry.live.store(false, Ordering::Release); }
            if entries.len() >= 32 { return None; }
            let session = sessions::get_session_by_session_id(&session_id)?;
            let token = owner.to_owned();
            let (tx, rx) = sync_channel::<RemoteAdbVideo>(8);
            let view = Arc::new(Mutex::new(View { generation: 0, state: "waiting".into(), error: String::new(), image: None, supported: false,
                adb_priority: false, touch_ready: false, input_error: String::new(), format: String::new(), screenshot_supported: false,
                privacy_supported: false, privacy_state: String::new(), privacy_error: String::new(),
                layout_supported: false, source_width: 0, source_height: 0 }));
            let resync = Arc::new(AtomicBool::new(true));
            let live = Arc::new(AtomicBool::new(true));
            entries.insert(session_id, Entry { peer: session.get_id(), token: token.clone(), queue: tx,
                view: view.clone(), resync: resync.clone(), live: live.clone(), since: Instant::now(), touch_sequence: 0 });
            let thread_token = token.clone();
            if std::thread::Builder::new().name("adb-video-decode".into()).spawn(move || {
                let mut decoder: Option<Decoder> = None;
                let mut generation = 0;
                let mut dimensions = (0, 0);
                let mut rgb = ImageRgb::new(ImageFormat::ABGR, 1);
                loop {
                    let packet = match rx.recv_timeout(Duration::from_millis(250)) {
                        Ok(packet) => packet,
                        Err(std::sync::mpsc::RecvTimeoutError::Disconnected) => break,
                        Err(std::sync::mpsc::RecvTimeoutError::Timeout) => {
                            if !live.load(Ordering::Acquire) { break; }
                            // Keep the subscription, but release codec buffers after video stops.
                            if view.lock().map(|v| v.state != "streaming").unwrap_or(true) {
                                decoder = None;
                                rgb = ImageRgb::new(ImageFormat::ABGR, 1);
                                resync.store(true, Ordering::Release);
                            }
                            continue;
                        }
                    };
                    if !live.load(Ordering::Acquire) { break; }
                    let result = (|| -> Result<(), String> {
                        if let Ok(view) = view.lock() {
                            if view.generation != packet.generation || view.state != "streaming" || view.format != packet.format { return Err(String::new()); }
                        }
                        if matches!(packet.format.as_str(), "jpeg" | "layout") {
                            decoder = None;
                            rgb = ImageRgb::new(ImageFormat::ABGR, 1);
                            let jpeg = image::codecs::jpeg::JpegDecoder::new(std::io::Cursor::new(&packet.data[..]))
                                .map_err(|_| "ADB 截图格式无效。".to_string())?;
                            if jpeg.dimensions() != (packet.width, packet.height) { return Err("ADB 截图尺寸无效。".into()); }
                            if let Ok(mut view) = view.lock() {
                                if view.generation == packet.generation && view.state == "streaming" && live.load(Ordering::Acquire) {
                                    view.image = Some(STANDARD.encode(&packet.data));
                                    view.source_width = packet.source_width;
                                    view.source_height = packet.source_height;
                                    view.error.clear();
                                }
                            }
                            return Ok(());
                        }
                        if packet.generation != generation || dimensions != (packet.width, packet.height) {
                            generation = packet.generation;
                            dimensions = (packet.width, packet.height);
                            decoder = None;
                            resync.store(true, Ordering::Release);
                        }
                        if packet.keyframe {
                            if resync.swap(false, Ordering::AcqRel) || decoder.is_none() {
                                decoder = Some(Decoder::new(CodecFormat::H264, None));
                            }
                        } else if resync.load(Ordering::Acquire) { return Err(String::new()); }
                        let decoder = decoder.as_mut().ok_or_else(String::new)?;
                        if !decoder.valid() { return Err("当前 PC 版本不支持 H.264 投屏，请更新完整客户端。".into()); }
                        let frames = EncodedVideoFrames { frames: vec![EncodedVideoFrame {
                            data: packet.data.clone(), key: packet.keyframe, ..Default::default() }], ..Default::default() };
                        let decoded = decoder.handle_video_frame(&video_frame::Union::H264s(frames),
                            &mut rgb, &mut ImageTexture::default(), &mut true, &mut None).map_err(|_| "ADB 视频解码失败，等待关键帧恢复。".to_string())?;
                        if decoded {
                            if rgb.w == 0 || rgb.h == 0 || rgb.w > 1280 || rgb.h > 1280 || rgb.raw.len() != rgb.w * rgb.h * 4 {
                                return Err("ADB 视频尺寸无效。".into());
                            }
                            let mut pixels = Vec::with_capacity(rgb.w * rgb.h * 3);
                            for rgba in rgb.raw.chunks_exact(4) { pixels.extend_from_slice(&rgba[..3]); }
                            let mut jpeg = Vec::new();
                            image::codecs::jpeg::JpegEncoder::new_with_quality(&mut jpeg, 85)
                                .encode(&pixels, rgb.w as u32, rgb.h as u32, image::ColorType::Rgb8)
                                .map_err(|_| "ADB 画面转换失败。".to_string())?;
                            if let Ok(mut view) = view.lock() {
                                if view.generation == generation && view.state == "streaming" && live.load(Ordering::Acquire) {
                                    view.image = Some(STANDARD.encode(jpeg));
                                    view.source_width = packet.source_width;
                                    view.source_height = packet.source_height;
                                    view.error.clear();
                                }
                            }
                        }
                        Ok(())
                    })();
                    if let Err(error) = &result {
                        resync.store(true, Ordering::Release);
                        if !error.is_empty() { if let Ok(mut view) = view.lock() {
                            if view.generation == packet.generation && view.state == "streaming" { view.error = error.clone(); }
                        } }
                    }
                    send(session_id, RemoteAdbVideo { token: thread_token.clone(),
                        action: if result.is_ok() { "ack" } else { "nack" }.into(), sequence: packet.sequence, ..Default::default() });
                }
            }).is_err() {
                entries.remove(&session_id);
                return None;
            }
            if !send(session_id, RemoteAdbVideo { token, action: "watch".into(), screenshot_supported: true, layout_supported: true, ..Default::default() }) {
                if let Some(entry) = entries.remove(&session_id) { entry.live.store(false, Ordering::Release); }
                return None;
            }
            return Some("{}".into());
        }
        if entries.get(&session_id).map(|entry| entry.token.as_str()) != Some(owner) { return None; }
        if action == "unwatch" {
            if let Some(entry) = entries.remove(&session_id) {
                entry.live.store(false, Ordering::Release);
                send(session_id, RemoteAdbVideo { token: entry.token, action: "unwatch".into(), ..Default::default() });
            }
            return Some("{}".into());
        }
        let entry = entries.get_mut(&session_id)?;
        if action == "poll" {
            let mut view = entry.view.lock().ok()?;
            if view.state == "waiting" && entry.since.elapsed() > Duration::from_secs(8) {
                view.state = "error".into();
                view.error = "远端未响应 ADB 投屏订阅，请确认两端均已更新并保持连接。".into();
            }
            return Some(json!({"state":view.state,"error":view.error,"generation":view.generation,"image":view.image.take(),"supported":view.supported,
                "adbPriority":view.adb_priority,"touchReady":view.touch_ready,"inputError":view.input_error,
                "format":view.format,"screenshotSupported":view.screenshot_supported,"layoutSupported":view.layout_supported,
                "sourceWidth":view.source_width,"sourceHeight":view.source_height,
                "privacySupported":view.privacy_supported,"privacyState":view.privacy_state,"privacyError":view.privacy_error}).to_string());
        }
        if action == "touch" {
            let view = entry.view.lock().ok()?;
            if !view.adb_priority || !view.touch_ready { return None; }
            if let Some(sequence) = request["sequence"].as_u64() {
                if sequence <= entry.touch_sequence { return None; }
                entry.touch_sequence = sequence;
            }
            let values: Option<Vec<i32>> = ["event", "x", "y", "width", "height"].iter()
                .map(|key| request[*key].as_i64().and_then(|v| i32::try_from(v).ok())).collect();
            let values = values?;
            if !(0..=2).contains(&values[0]) || !(1..=16384).contains(&values[3]) || !(1..=16384).contains(&values[4]) { return None; }
            let data: Vec<u8> = values.iter().flat_map(|value| value.to_be_bytes()).collect();
            if !send(session_id, RemoteAdbVideo { token: entry.token.clone(), action: "touch".into(), data: data.into(), ..Default::default() }) { return None; }
            return Some("{}".into());
        }
        if !matches!(action, "start" | "screenshot" | "layout" | "stop" | "black_on" | "black_off") { return None; }
        if action == "screenshot" && !entry.view.lock().ok()?.screenshot_supported { return None; }
        if action == "layout" && !entry.view.lock().ok()?.layout_supported { return None; }
        if matches!(action, "black_on" | "black_off") && !entry.view.lock().ok()?.privacy_supported { return None; }
        if !send(session_id, RemoteAdbVideo { token: entry.token.clone(), action: action.into(), ..Default::default() }) { return None; }
        Some("{}".into())
    }

    pub fn receive(peer: &str, packet: RemoteAdbVideo) {
        if packet.data.len() > MAX_FRAME || packet.error.len() > 2048 || packet.input_error.len() > 2048 || packet.state.len() > 16
            || packet.privacy_error.len() > 2048 || packet.privacy_state.len() > 16
            || !matches!(packet.format.as_str(), "" | "h264" | "jpeg" | "layout")
            || packet.source_width > 16384 || packet.source_height > 16384
            || (packet.source_width == 0) != (packet.source_height == 0) { return; }
        let Ok(mut entries) = ENTRIES.lock() else { return; };
        let Some((session_id, entry)) = entries.iter_mut().find(|(_, entry)| entry.peer == peer && entry.token == packet.token) else { return; };
        if packet.action == "state" {
            if !packet.data.is_empty() { return; }
            if let Ok(mut view) = entry.view.lock() {
                if packet.generation < view.generation { return; }
                if packet.state != "streaming" || packet.generation != view.generation {
                    view.image = None;
                    entry.resync.store(true, Ordering::Release);
                }
                view.generation = packet.generation;
                view.supported = packet.supported;
                view.adb_priority = packet.adb_priority;
                view.touch_ready = packet.touch_ready;
                view.input_error = packet.input_error;
                view.format = packet.format;
                view.screenshot_supported = packet.screenshot_supported;
                view.layout_supported = packet.layout_supported;
                view.privacy_supported = packet.privacy_supported;
                view.privacy_state = packet.privacy_state;
                view.privacy_error = packet.privacy_error;
                view.state = packet.state;
                view.error = packet.error;
            }
        } else if packet.action == "frame" {
            if packet.width == 0 || packet.height == 0 || packet.width > 1280 || packet.height > 1280 || packet.data.is_empty() { return; }
            if let Err(error) = entry.queue.try_send(packet) {
                entry.resync.store(true, Ordering::Release);
                let packet = match error { std::sync::mpsc::TrySendError::Full(p) | std::sync::mpsc::TrySendError::Disconnected(p) => p };
                send(*session_id, RemoteAdbVideo { token: entry.token.clone(), action: "nack".into(), sequence: packet.sequence, ..Default::default() });
            }
        }
    }
}
