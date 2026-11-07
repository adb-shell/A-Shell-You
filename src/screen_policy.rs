//! Per-device screen policy. Only authenticated remote sessions may be registered.
//! This module owns no transport, capture, input or voice-call resources.
use std::{collections::HashMap, time::Instant};

#[derive(Clone, Copy, Debug)]
pub(crate) struct Access {
    pub paused: bool,
    pub revision: u64,
    pub changed_at: Instant,
}

#[derive(Default)]
pub(crate) struct ScreenPolicy {
    members: HashMap<i32, (bool, Access)>,
    revision: u64,
}

impl ScreenPolicy {
    pub fn register(&mut self, id: i32, developer: bool) -> Vec<(i32, Access)> {
        // Duplicate login/cleanup must not change an established session's role.
        if self.members.contains_key(&id) { return Vec::new(); }
        self.members.insert(id, (developer, Access {
            paused: false, revision: 0, changed_at: Instant::now(),
        }));
        self.reconcile()
    }

    pub fn remove(&mut self, id: i32) -> Vec<(i32, Access)> {
        if self.members.remove(&id).is_none() { return Vec::new(); }
        self.reconcile()
    }

    pub fn access(&self, id: i32) -> Option<Access> {
        self.members.get(&id).map(|(_, access)| *access)
    }

    pub fn is_developer(&self, id: i32) -> bool {
        self.members.get(&id).map(|(developer, _)| *developer).unwrap_or(false)
    }

    fn reconcile(&mut self) -> Vec<(i32, Access)> {
        self.revision += 1;
        let exclusive = self.members.values().any(|(developer, _)| *developer);
        let now = Instant::now();
        let mut changed = Vec::new();
        for (id, (developer, access)) in &mut self.members {
            let paused = exclusive && !*developer;
            if access.revision == 0 || access.paused != paused {
                *access = Access { paused, revision: self.revision, changed_at: now };
                changed.push((*id, *access));
            }
        }
        changed
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn ordinary_first_is_paused_and_restored_without_removing_membership() {
        let mut p = ScreenPolicy::default();
        p.register(1, false);
        assert!(!p.access(1).unwrap().paused);
        p.register(2, true);
        assert!(p.access(1).unwrap().paused);
        assert!(!p.access(2).unwrap().paused);
        p.remove(2);
        assert!(!p.access(1).unwrap().paused);
    }

    #[test]
    fn ordinary_join_and_reconnect_never_acquire_video_during_exclusivity() {
        let mut p = ScreenPolicy::default();
        p.register(2, true);
        let changed = p.register(1, false);
        assert!(changed.iter().any(|(id, a)| *id == 1 && a.paused));
        p.remove(1);
        p.register(3, false);
        assert!(p.access(3).unwrap().paused);
        assert!(!p.access(2).unwrap().paused);
    }

    #[test]
    fn last_developer_releases_and_duplicate_cleanup_is_harmless() {
        let mut p = ScreenPolicy::default();
        p.register(1, false);
        p.register(2, true);
        p.register(3, true);
        p.remove(2);
        assert!(p.access(1).unwrap().paused);
        assert!(!p.access(3).unwrap().paused);
        assert!(p.remove(2).is_empty());
        let paused_revision = p.access(1).unwrap().revision;
        p.remove(3);
        let resumed = p.access(1).unwrap();
        assert!(!resumed.paused && resumed.revision > paused_revision);
    }

    #[test]
    fn duplicate_registration_cannot_promote_an_ordinary_connection() {
        let mut p = ScreenPolicy::default();
        p.register(1, false);
        assert!(p.register(1, true).is_empty());
        p.register(2, false);
        assert!(!p.access(2).unwrap().paused);
    }
}
