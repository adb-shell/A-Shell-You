//! 服务器地址的唯一维护入口

use serde_derive::Serialize;
use std::sync::atomic::{AtomicU64, Ordering};

static GENERATION: AtomicU64 = AtomicU64::new(0);
pub fn generation() -> u64 { GENERATION.load(Ordering::SeqCst) }
pub fn changed() { GENERATION.fetch_add(1, Ordering::SeqCst); }

pub const PROFILE_OPTION: &str = "server-profile";
pub const REVISION_OPTION: &str = "server-profile-revision";
pub const PROFILE_STATE_OPTION: &str = "server-profile-state";
pub const PROFILE_ERROR_OPTION: &str = "server-profile-error";
pub const SERVER_PUBLIC_KEY: &str = "TUNNEL8XH7AKM4NWR5SZC3VBE6FD2L9AT8XH4KM7NS=";
pub const ENDPOINT_KEYS: &[&str] = &[
    "custom-rendezvous-server", "relay-server", "api-server", "key",
];

#[derive(Clone, Copy, Debug, Serialize)]
pub struct ServerProfile {
    pub id: &'static str,
    pub name: &'static str,
    pub id_server: &'static str,
    pub relay_server: &'static str,
    pub api_server: &'static str,
    pub voice_token_url: &'static str,
}

// Android配置
pub const ANDROID: ServerProfile = ServerProfile {
    id: "android",
    name: "Android",
    id_server: "216.236.63.58:10056",
    relay_server: "216.236.63.58:10057",
    api_server: "http://216.236.63.58:10054",
    voice_token_url: "http://216.236.63.58:10053",
};

// PC配置1
pub const PC_1: ServerProfile = ServerProfile {
    id: "pc_1",
    name: "服务器 1",
    id_server: "216.236.63.58:10056",
    relay_server: "216.236.63.58:10057",
    api_server: "http://216.236.63.58:10054",
    voice_token_url: "http://216.236.63.58:10053",
};

// PC配置2
pub const PC_2: ServerProfile = ServerProfile {
    id: "pc_2",
    name: "服务器 2",
    id_server: "43.255.158.17:10056",
    relay_server: "43.255.158.17:10057",
    api_server: "http://43.255.158.17:10054",
    voice_token_url: "http://43.255.158.17:10053",
};

pub const PC_PROFILES: &[ServerProfile] = &[PC_1, PC_2];

pub fn desktop_profile(id: &str) -> Option<&'static ServerProfile> {
    PC_PROFILES.iter().find(|profile| profile.id == id)
}

pub fn selected_profile(id: &str) -> &'static ServerProfile {
    #[cfg(any(target_os = "android", target_os = "ios"))]
    { let _ = id; &ANDROID }
    #[cfg(not(any(target_os = "android", target_os = "ios")))]
    { desktop_profile(id).unwrap_or(&PC_1) }
}

impl ServerProfile {
    pub fn validate(&self) -> Result<(), &'static str> {
        for address in [self.id_server, self.relay_server] {
            let url = url::Url::parse(&format!("tcp://{}", address))
                .map_err(|_| "ID或中继地址无效，请填写host:port")?;
            if url.host_str().is_none() || url.port().filter(|port| *port > 0).is_none()
                || !url.username().is_empty() || url.password().is_some()
                || !matches!(url.path(), "" | "/") || url.query().is_some() || url.fragment().is_some()
            {
                return Err("ID或中继地址无效，请填写host:port");
            }
        }
        for address in [self.api_server, self.voice_token_url] {
            let url = url::Url::parse(address).map_err(|_| "API或语音Token服务地址未配置")?;
            if !matches!(url.scheme(), "http" | "https") || url.host_str().is_none()
                || !url.username().is_empty() || url.password().is_some() || url.fragment().is_some()
            {
                return Err("API或语音Token服务地址无效");
            }
        }
        Ok(())
    }

    pub fn option(&self, key: &str) -> Option<String> {
        let value = match key {
            "custom-rendezvous-server" => self.id_server,
            "relay-server" => self.relay_server,
            "api-server" => self.api_server,
            "key" => SERVER_PUBLIC_KEY,
            _ => return None,
        };
        Some(value.to_owned())
    }

    pub fn data_suffix(&self) -> &'static str {
        if self.id == PC_2.id { "_pc_2" } else { "" }
    }
}
#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn unknown_selection_uses_default_and_platform_is_fixed() {
        #[cfg(not(any(target_os = "android", target_os = "ios")))]
        assert_eq!(selected_profile("unknown").id, PC_1.id);
        #[cfg(any(target_os = "android", target_os = "ios"))]
        assert_eq!(selected_profile(PC_2.id).id, ANDROID.id);
    }

    #[test]
    fn incomplete_profile_cannot_be_selected() {
        let profile = ServerProfile { relay_server: "", ..PC_1 };
        assert!(profile.validate().is_err());
    }

    #[test]
    fn environments_have_distinct_storage_and_fixed_key() {
        assert_ne!(PC_1.data_suffix(), PC_2.data_suffix());
        assert_eq!(PC_1.option("key"), PC_2.option("key"));
        assert_eq!(ANDROID.option("key"), PC_1.option("key"));
    }

    #[test]
    fn validates_complete_addresses_and_rejects_partial_or_credential_urls() {
        let profile = ServerProfile {
            id_server: "id.example.invalid:21116",
            relay_server: "relay.example.invalid:21117",
            api_server: "https://api.example.invalid",
            voice_token_url: "https://voice.example.invalid/token",
            ..PC_1
        };
        assert!(profile.validate().is_ok());
        for address in ["", "id.example.invalid", "id.example.invalid:0", "user@id.example.invalid:21116"] {
            assert!(ServerProfile { id_server: address, ..profile }.validate().is_err());
        }
        for address in ["", "api.example.invalid", "ftp://api.example.invalid", "https://user@api.example.invalid"] {
            assert!(ServerProfile { api_server: address, ..profile }.validate().is_err());
        }
    }
}
