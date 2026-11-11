//! Desktop profile switching. Addresses live only in hbb_common::server_profiles.
use hbb_common::{config::{Config, LocalConfig}, server_profiles, ResultType, bail};
use std::sync::Mutex;

// Shares the critical section with session creation in every Flutter engine.
pub static CHANGE_LOCK: Mutex<()> = Mutex::new(());
static LAST_ERROR: Mutex<String> = Mutex::new(String::new());

pub fn state() -> String {
    let profiles: Vec<_> = server_profiles::PC_PROFILES.iter().map(|profile| {
        serde_json::json!({"profile": profile, "configured": profile.validate().is_ok()})
    }).collect();
    serde_json::json!({"active": Config::server_profile().id, "generation": server_profiles::generation(), "profiles": profiles}).to_string()
}

pub fn last_error() -> String { LAST_ERROR.lock().unwrap().clone() }

pub fn select(id: &str) {
    let _guard = CHANGE_LOCK.lock().unwrap();
    *LAST_ERROR.lock().unwrap() = String::new();
    if let Err(error) = select_inner(id) {
        *LAST_ERROR.lock().unwrap() = error.to_string();
    }
}

fn select_inner(id: &str) -> ResultType<()> {
    if id == Config::server_profile().id { return Ok(()); }
    let profile = server_profiles::desktop_profile(id)
        .ok_or_else(|| hbb_common::anyhow::anyhow!("未知服务器配置"))?;
    profile.validate().map_err(|error| hbb_common::anyhow::anyhow!(error))?;
    #[cfg(feature = "flutter")]
    if !crate::flutter::sessions::get_sessions().is_empty() {
        bail!("请先结束所有远控、文件、终端和语音连接");
    }
    if crate::hbbs_http::account::OidcSession::is_running() {
        bail!("请先完成或取消当前登录");
    }
    crate::ipc::switch_server_profile(id)?;
    // Every environment requires its own login; never forward an old Bearer token.
    for key in ["access_token", "user_info", "user_email"] {
        LocalConfig::set_option(key.to_owned(), String::new());
    }
    crate::common::LOGIN_USERNAME.lock().unwrap().clear();
    crate::ui_interface::refresh_options();
    crate::ui_interface::reset_connect_status();
    Ok(())
}
