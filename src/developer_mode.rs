//! Cooperative developer screen priority; this is not a credential authority.
//! The existing endpoint login is still required. No developer password crosses the wire.
use std::sync::atomic::{AtomicBool, Ordering};

static ENABLED: AtomicBool = AtomicBool::new(false);

pub(crate) fn enabled() -> bool {
    !cfg!(any(target_os = "android", target_os = "ios")) && ENABLED.load(Ordering::Acquire)
}

/// All desktop Flutter engines share the loaded Rust library and this process flag.
#[no_mangle]
pub extern "C" fn openworld_enable_developer_mode() {
    if !cfg!(any(target_os = "android", target_os = "ios")) {
        ENABLED.store(true, Ordering::Release);
    }
}

pub(crate) fn video_allowed(id: i32) -> bool {
    #[cfg(target_os = "android")]
    return android::access(id).map(|a| !a.paused).unwrap_or(true);
    #[cfg(not(target_os = "android"))]
    { let _ = id; true }
}

pub(crate) fn frame_allowed(id: i32, queued_at: std::time::Instant) -> bool {
    #[cfg(target_os = "android")]
    return android::access(id)
        .map(|a| !a.paused && queued_at >= a.changed_at).unwrap_or(true);
    #[cfg(not(target_os = "android"))]
    { let _ = (id, queued_at); true }
}

pub(crate) fn is_screen_frame(msg: &hbb_common::message_proto::Message) -> bool {
    use hbb_common::message_proto::{message, misc};
    match &msg.union {
        Some(message::Union::VideoFrame(_)) => true,
        // Empty screenshot errors still complete an outstanding request while paused.
        Some(message::Union::ScreenshotResponse(response)) => !response.data.is_empty(),
        Some(message::Union::Misc(m)) => matches!(&m.union,
            Some(misc::Union::RemoteAdbVideo(p)) if p.action == "frame"),
        _ => false,
    }
}

#[cfg(target_os = "android")]
pub(crate) mod android {
    use crate::{screen_policy::{Access, ScreenPolicy}, server::Sender};
    use hbb_common::{message_proto::{Message, Misc, ScreenAccess}, tokio::time::Instant};
    use std::{collections::HashMap, sync::{Arc, Mutex}};

    #[derive(Default)]
    struct Registry { policy: ScreenPolicy, senders: HashMap<i32, Sender> }
    lazy_static::lazy_static! { static ref REGISTRY: Mutex<Registry> = Mutex::new(Registry::default()); }

    fn notify(registry: &Registry, changes: Vec<(i32, Access)>) {
        // Queue only; never call JNI, acquire a video lock or await while holding REGISTRY.
        for (id, access) in changes {
            if let Some(tx) = registry.senders.get(&id) {
                let mut misc = Misc::new();
                misc.set_screen_access(ScreenAccess {
                    paused: access.paused, revision: access.revision,
                    developer_mode: registry.policy.is_developer(id), ..Default::default()
                });
                let mut msg = Message::new();
                msg.set_misc(misc);
                let _ = tx.send((Instant::now(), Arc::new(msg)));
            }
        }
    }

    pub fn register(id: i32, developer: bool, tx: Sender) {
        let mut registry = REGISTRY.lock().unwrap();
        registry.senders.insert(id, tx);
        let changes = registry.policy.register(id, developer);
        notify(&registry, changes);
    }

    pub fn remove(id: i32) {
        let mut registry = REGISTRY.lock().unwrap();
        registry.senders.remove(&id);
        let changes = registry.policy.remove(id);
        notify(&registry, changes);
    }

    pub fn access(id: i32) -> Option<Access> { REGISTRY.lock().unwrap().policy.access(id) }
}
