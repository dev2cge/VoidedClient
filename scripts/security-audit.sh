#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$project_root"

runtime_roots=(common/src fabric/src forge/src)
blocked='java\.net\.|java\.net\.http|okhttp|apache\.http|Runtime\.getRuntime\(\)\.exec|ProcessBuilder|System\.getenv|discord.*token|webhook|access[_-]?token|refresh[_-]?token'

if grep -RInE "$blocked" "${runtime_roots[@]}" --include='*.java' --include='*.json' --include='*.toml'; then
  echo "Security audit found an external-network, process, environment-secret or token pattern." >&2
  echo "Review the change and update SECURITY.md and PRIVACY.md before deliberately adjusting this guard." >&2
  exit 1
fi

if find "${runtime_roots[@]}" -type f \( -name '*.mixins.json' -o -name '*mixin*.json' \) | grep -q .; then
  echo "Security audit found a mixin configuration; VoidedClient currently promises no mixins." >&2
  exit 1
fi

echo "VoidedClient focused runtime security audit passed."
