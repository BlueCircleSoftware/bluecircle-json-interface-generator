#!/usr/bin/env bash

set -euo pipefail

# When doing a release, update the README with the latest version numbers
# Parameter 1: path to project root

root="${1:?Usage: $0 /path/to/project-root}"
release_props="${root}/release.properties"
readme="${root}/README.md"
pom="${root}/pom.xml"

if [[ ! -f "${release_props}" ]]; then
  echo "release.properties not found at ${release_props}" >&2
  exit 1
fi
if [[ ! -f "${readme}" ]]; then
  echo "README.md not found at ${readme}" >&2
  exit 1
fi
if [[ ! -f "${pom}" ]]; then
  echo "pom.xml not found at ${pom}" >&2
  exit 1
fi

artifact_id="$(grep -m1 -E "<artifactId>" "${pom}" | sed -E "s/.*<artifactId>([^<]+).*/\\1/")"
group_id="$(grep -m1 -E "<groupId>" "${pom}" | sed -E "s/.*<groupId>([^<]+).*/\\1/")"

version=""
prop_key="project.rel.${group_id}:${artifact_id}"
if line="$(grep -F "${prop_key}=" "${release_props}" | head -n1)"; then
  version="${line#*=}"
elif line="$(grep -E "^scm.tag=" "${release_props}" | head -n1)"; then
  tag="${line#scm.tag=}"
  if [[ "${tag}" == "${artifact_id}-"* ]]; then
    version="${tag#${artifact_id}-}"
  else
    version="${tag}"
  fi
fi

if [[ -z "${version}" ]]; then
  echo "Could not determine release version from ${release_props}" >&2
  exit 1
fi

# Update versions in README and refresh release badge.
sed -i "s|<version>.*</version> <!-- latest version -->|<version>${version}</version> <!-- latest version -->|g" "${readme}"
sed -i "s|badge/release-[0-9A-Za-z._-]*-blue|badge/release-${version}-blue|g" "${readme}"
