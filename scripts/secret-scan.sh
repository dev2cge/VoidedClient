#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$project_root"

patterns='-----BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY-----|AKIA[0-9A-Z]{16}|gh[pousr]_[A-Za-z0-9]{36,}|github_pat_[A-Za-z0-9_]{40,}|https://discord(app)?\.com/api/webhooks/[0-9]+/[A-Za-z0-9._-]+'

if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  if git grep -nEI -- "$patterns" -- . ':!scripts/secret-scan.sh'; then
    echo "Potential committed credential detected." >&2
    exit 1
  fi
else
  if grep -RInE --exclude='secret-scan.sh' --exclude-dir='.gradle' --exclude-dir='build' -- "$patterns" .; then
    echo "Potential credential detected in source bundle." >&2
    exit 1
  fi
fi

echo "High-confidence secret-pattern scan passed."
