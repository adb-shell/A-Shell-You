#!/usr/bin/env bash
# Ubuntu 22.04 hosted runners only. Keep APT sources/state local to this CI step.
set -euo pipefail

if (( $# == 0 )); then
  echo 'No dependency packages supplied' >&2
  exit 2
fi

# RUNNER_TEMP can have private ancestors; chmod on a child cannot fix that.
# /tmp is traversable by _apt. Keep this root-owned directory for runner cleanup.
apt_dir="$(sudo mktemp -d /tmp/openworld-apt.XXXXXX)"
sudo chmod 755 "$apt_dir"
sudo install -d -m 755 "$apt_dir/sources.list.d"
if ! sudo -u _apt test -x "$apt_dir" || ! sudo -u _apt test -r /etc/resolv.conf; then
  echo '::error::APT sandbox cannot access its directory or the system resolver configuration'
  exit 1
fi

apt_common_options=(
  -o "Dir::Etc::sourceparts=$apt_dir/sources.list.d"
  -o 'Dir::Cache::pkgcache='
  -o 'Dir::Cache::srcpkgcache='
  -o 'APT::Sandbox::User=_apt'
  -o 'Acquire::Retries=2'
  -o 'Acquire::http::Timeout=30'
  -o 'Acquire::https::Timeout=30'
  -o 'DPkg::Lock::Timeout=60'
)

# All candidates serve Ubuntu-signed metadata, checked by apt using this keyring.
# Do not replace DNS/hosts, pin IPs, disable TLS, or trust a mirror's own signing key.
archives=(https://archive.ubuntu.com/ubuntu https://mirrors.kernel.org/ubuntu https://mirrors.mit.edu/ubuntu)
security_archives=(https://security.ubuntu.com/ubuntu https://mirrors.kernel.org/ubuntu https://mirrors.mit.edu/ubuntu)
probe_index() {
  sudo -u _apt timeout --signal=TERM --kill-after=5s 15s \
    curl --fail --silent --show-error --location --max-redirs 3 \
    --proto '=https' --proto-redir '=https' --connect-timeout 5 --max-time 10 \
    --output /dev/null "$1/dists/$2/InRelease"
}

selected=false
for attempt in "${!archives[@]}"; do
  archive="${archives[$attempt]}"
  security_archive="${security_archives[$attempt]}"
  echo "Checking Ubuntu indexes via $archive and $security_archive as _apt"
  if ! probe_index "$archive" jammy || ! probe_index "$archive" jammy-updates \
      || ! probe_index "$security_archive" jammy-security; then
    echo '::warning::Ubuntu mirror probe failed; trying the next signed mirror'
    continue
  fi

  # Never mix partial/stale indexes from a failed candidate with another mirror.
  lists="$apt_dir/lists-$attempt"
  source_list="$apt_dir/sources-$attempt.list"
  sudo install -d -m 755 "$lists"
  sudo install -d -o _apt -g root -m 700 "$lists/partial"
  printf 'deb [signed-by=/usr/share/keyrings/ubuntu-archive-keyring.gpg] %s %s main restricted universe multiverse\n' \
    "$archive" jammy "$archive" jammy-updates "$security_archive" jammy-security \
    | sudo tee "$source_list" > /dev/null
  sudo chmod 644 "$source_list"
  if ! sudo -u _apt test -r "$source_list" || ! sudo -u _apt test -w "$lists/partial"; then
    echo '::error::APT sandbox cannot read sources or write partial indexes'
    exit 1
  fi
  apt_options=("${apt_common_options[@]}" -o "Dir::Etc::sourcelist=$source_list" -o "Dir::State::lists=$lists")
  echo 'Updating signed Ubuntu indexes (3 minute limit per candidate)'
  if sudo timeout --signal=TERM --kill-after=15s 3m \
      apt-get "${apt_options[@]}" -o 'APT::Update::Error-Mode=any' update; then
    selected=true
    break
  fi
  echo '::warning::Signed index update failed; trying the next mirror with fresh indexes'
done

if [[ "$selected" != true ]]; then
  echo '::error::No Ubuntu source passed sandboxed HTTPS probing and signed index update; installation was not attempted'
  exit 1
fi

echo 'Installing Ubuntu build dependencies (10 minute limit)'
sudo env DEBIAN_FRONTEND=noninteractive \
  timeout --signal=TERM --kill-after=15s 10m \
  apt-get "${apt_options[@]}" install -y --no-install-recommends "$@"
