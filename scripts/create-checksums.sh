#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$project_root"

mapfile -t artifacts < <(find fabric/build/libs forge/build/libs -maxdepth 1 -type f -name '*.jar' \
  ! -name '*-sources.jar' ! -name '*-dev.jar' | LC_ALL=C sort)

if [[ ${#artifacts[@]} -eq 0 ]]; then
  echo "No release JARs found. Build Fabric and Forge first." >&2
  exit 1
fi

: > SHA256SUMS.txt
for artifact in "${artifacts[@]}"; do
  hash="$(sha256sum "$artifact" | awk '{print $1}')"
  printf '%s  %s\n' "$hash" "$(basename "$artifact")" >> SHA256SUMS.txt
done
echo "Wrote SHA256SUMS.txt for ${#artifacts[@]} artifact(s)."
