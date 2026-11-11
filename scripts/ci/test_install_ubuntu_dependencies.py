"""Offline control-flow tests; no sudo, APT, curl, or network is executed.

Run with Python 3.9+ and Bash. Set OPENWORLD_TEST_BASH if Bash is not on PATH.
Linux ownership/DNS/HTTPS/signature behavior must still be verified on the runner.
"""
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/ci/install_ubuntu_dependencies.sh"
BASH = os.environ.get("OPENWORLD_TEST_BASH") or shutil.which("bash")
WRAPPER = r'''
set -euo pipefail
sudo() {
  printf 'sudo %s\n' "$*" >> "$TRACE"
  if [[ "$1" == -u ]]; then
    [[ "$2" == _apt ]]; shift 2
    if [[ "$1" == test ]]; then
      if [[ "${FAIL_ACCESS:-}" == traverse && "$2" == -x ]]; then return 1; fi
      if [[ "${FAIL_ACCESS:-}" == partial && "$2" == -w ]]; then return 1; fi
      if [[ "$3" == /etc/resolv.conf ]]; then return 0; fi
    fi
  fi
  if [[ "$1" == env ]]; then shift; export "$1"; shift; fi
  if [[ "$1" == mktemp ]]; then
    [[ "$2" == -d && "$3" == /tmp/openworld-apt.XXXXXX ]]
    command mktemp -d "$MOCK_ROOT/openworld-apt.XXXXXX"
    return
  fi
  if [[ "$1" == install ]]; then
    shift
    while [[ "$1" == -* ]]; do
      case "$1" in -d) shift ;; -m|-o|-g) shift 2 ;; *) return 99 ;; esac
    done
    command mkdir -p "$@"
    return
  fi
  "$@"
}
timeout() {
  printf 'timeout %s %s %s\n' "$1" "$2" "$3" >> "$TRACE"
  local duration="$3"; shift 3
  if [[ "${MOCK_TIMEOUT:-}" == "$duration" ]]; then return 124; fi
  "$@"
}
curl() {
  local url="${!#}" candidate
  case "$url" in
    https://archive.ubuntu.com/*|https://security.ubuntu.com/*) candidate=0 ;;
    https://mirrors.kernel.org/*) candidate=1 ;;
    https://mirrors.mit.edu/*) candidate=2 ;;
    *) return 99 ;;
  esac
  printf 'probe %s %s\n' "$candidate" "$url" >> "$TRACE"
  if [[ ",${FAIL_PROBES:-}," == *",$candidate,"* ]]; then return 6; fi
  if [[ "${FAIL_SECURITY:-}" == yes && "$url" == https://security.ubuntu.com/* ]]; then return 6; fi
}
apt-get() {
  local stage= arg source_list= lists= candidate
  for arg in "$@"; do
    case "$arg" in
      update|install) stage="$arg" ;;
      Dir::Etc::sourcelist=*) source_list="${arg#*=}" ;;
      Dir::State::lists=*) lists="${arg#*=}" ;;
    esac
  done
  candidate="${source_list##*/sources-}"; candidate="${candidate%.list}"
  printf 'apt %s %s\n' "$stage" "$candidate" >> "$TRACE"
  printf 'apt-options <%s>\n' "$@" >> "$TRACE"
  cp "$source_list" "$MOCK_ROOT/observed-$candidate.list"
  if [[ "$stage" == update ]]; then
    [[ ! -f "$lists/old-index" ]]
    printf 'partial index' > "$lists/old-index"
    if [[ ",${FAIL_UPDATES:-}," == *",$candidate,"* ]]; then return 100; fi
  else
    [[ "${DEBIAN_FRONTEND:-}" == noninteractive ]]
    return "${INSTALL_EXIT:-0}"
  fi
}
script_path="$1"; shift
source "$script_path" "$@"
'''


@unittest.skipUnless(BASH, "Bash is required")
class InstallDependenciesTest(unittest.TestCase):
    def run_case(self, settings=None, packages=("pkg-config", "clang")):
        # Restrict cleanup to a directory created inside this checkout.
        with tempfile.TemporaryDirectory(prefix="ow apt test-", dir=ROOT) as tmp:
            folder = Path(tmp).resolve()
            self.assertEqual(folder.parent, ROOT)
            trace = folder / "trace.txt"
            env = dict(os.environ, MOCK_ROOT=folder.as_posix(), TRACE=trace.as_posix())
            env.pop("RUNNER_TEMP", None)
            env.update(settings or {})
            result = subprocess.run(
                [BASH, "--noprofile", "--norc", "-c", WRAPPER, "test", SCRIPT.as_posix(), *packages],
                env=env, text=True, capture_output=True, timeout=20,
            )
            events = trace.read_text() if trace.exists() else ""
            sources = {p.name: p.read_text() for p in folder.glob("observed-*.list")}
            return result.returncode, events, sources, result.stderr

    def assert_selected(self, result, candidate):
        code, events, sources, stderr = result
        self.assertEqual(code, 0, stderr)
        self.assertIn(f"apt install {candidate}\n", events)
        self.assertIn("apt-options <APT::Sandbox::User=_apt>", events)
        self.assertIn("apt-options <APT::Update::Error-Mode=any>", events)
        self.assertIn("sudo install -d -o _apt -g root -m 700", events)
        self.assertIn("timeout --signal=TERM --kill-after=15s 10m", events)
        self.assertIn("apt-options <pkg-config>", events)
        self.assertIn("apt-options <clang>", events)
        for content in sources.values():
            self.assertEqual(content.count("signed-by=/usr/share/keyrings/ubuntu-archive-keyring.gpg"), 3)
            self.assertTrue(all("https://" in line for line in content.splitlines()))
            self.assertNotIn("trusted=yes", content)

    def test_primary_success_without_runner_temp(self):
        self.assert_selected(self.run_case(), 0)

    def test_primary_dns_failure_uses_kernel_mirror(self):
        self.assert_selected(self.run_case({"FAIL_PROBES": "0"}), 1)

    def test_first_two_mirrors_fail_uses_mit(self):
        self.assert_selected(self.run_case({"FAIL_PROBES": "0,1"}), 2)

    def test_security_suite_probe_failure_uses_next_mirror(self):
        self.assert_selected(self.run_case({"FAIL_SECURITY": "yes"}), 1)

    def test_update_failure_switches_to_fresh_indexes(self):
        self.assert_selected(self.run_case({"FAIL_UPDATES": "0"}), 1)

    def test_no_install_when_all_probes_fail(self):
        code, events, _, _ = self.run_case({"FAIL_PROBES": "0,1,2"})
        self.assertEqual(code, 1)
        self.assertNotIn("apt update", events)
        self.assertNotIn("apt install", events)

    def test_no_install_when_all_index_updates_fail(self):
        code, events, _, _ = self.run_case({"FAIL_UPDATES": "0,1,2"})
        self.assertEqual(code, 1)
        self.assertEqual(events.count("apt update"), 3)
        self.assertNotIn("apt install", events)

    def test_update_timeouts_do_not_install(self):
        code, events, _, _ = self.run_case({"MOCK_TIMEOUT": "3m"})
        self.assertEqual(code, 1)
        self.assertNotIn("apt install", events)

    def test_install_failure_propagates_without_reinstall(self):
        code, events, _, _ = self.run_case({"INSTALL_EXIT": "100"})
        self.assertEqual(code, 100)
        self.assertEqual(events.count("apt install"), 1)

    def test_install_timeout_propagates(self):
        code, _, _, _ = self.run_case({"MOCK_TIMEOUT": "10m"})
        self.assertEqual(code, 124)

    def test_sandbox_traversal_failure_stops_before_network(self):
        code, events, _, _ = self.run_case({"FAIL_ACCESS": "traverse"})
        self.assertEqual(code, 1)
        self.assertNotIn("probe ", events)

    def test_sandbox_write_failure_stops_before_apt(self):
        code, events, _, _ = self.run_case({"FAIL_ACCESS": "partial"})
        self.assertEqual(code, 1)
        self.assertNotIn("apt update", events)

    def test_no_packages_stops_without_mutations(self):
        code, events, _, _ = self.run_case(packages=())
        self.assertEqual(code, 2)
        self.assertEqual(events, "")


if __name__ == "__main__":
    unittest.main()
