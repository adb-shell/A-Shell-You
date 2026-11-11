#!/usr/bin/env bash
# Ubuntu 22.04 hosted runners only. Keep APT sources/state local to this CI step.
set -euo pipefail

: "${RUNNER_TEMP:?RUNNER_TEMP must identify the hosted runner temporary directory}"
if (( $# == 0 )); then
  echo 'No dependency packages supplied' >&2
  exit 2
fi

apt_dir="$(mktemp -d "$RUNNER_TEMP/openworld-apt.XXXXXX")"
chmod 755 "$apt_dir"
mkdir -p "$apt_dir/lists/partial" "$apt_dir/sources.list.d"
# Bypass the runner's mirror+file list (including azure.archive.ubuntu.com).
# An empty list directory prevents a failed update from using stale runner indexes.
cat > "$apt_dir/sources.list" <<'SOURCES'
deb [signed-by=/usr/share/keyrings/ubuntu-archive-keyring.gpg] https://archive.ubuntu.com/ubuntu jammy main restricted universe multiverse
deb [signed-by=/usr/share/keyrings/ubuntu-archive-keyring.gpg] https://archive.ubuntu.com/ubuntu jammy-updates main restricted universe multiverse
deb [signed-by=/usr/share/keyrings/ubuntu-archive-keyring.gpg] https://security.ubuntu.com/ubuntu jammy-security main restricted universe multiverse
SOURCES

apt_options=(
  -o "Dir::Etc::sourcelist=$apt_dir/sources.list"
  -o "Dir::Etc::sourceparts=$apt_dir/sources.list.d"
  -o "Dir::State::lists=$apt_dir/lists"
  -o 'Dir::Cache::pkgcache='
  -o 'Dir::Cache::srcpkgcache='
  -o 'Acquire::Retries=2'
  -o 'Acquire::http::Timeout=30'
  -o 'Acquire::https::Timeout=30'
  -o 'DPkg::Lock::Timeout=60'
)

echo 'Updating Ubuntu package indexes (5 minute limit; any index error is fatal)'
sudo timeout --signal=TERM --kill-after=15s 5m \
  apt-get "${apt_options[@]}" -o 'APT::Update::Error-Mode=any' update

echo 'Installing Ubuntu build dependencies (10 minute limit)'
sudo env DEBIAN_FRONTEND=noninteractive \
  timeout --signal=TERM --kill-after=15s 10m \
  apt-get "${apt_options[@]}" install -y --no-install-recommends "$@"
