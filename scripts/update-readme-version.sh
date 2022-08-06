#!/usr/bin/env bash

set -ex

# When doing a release, update the README with the latest version numbers
# Parameter 1: path to project root

version=$(grep -E "^scm.tag=" <"$1"/release.properties | sed "s/^scm.tag=json-interface-generator-//")
sed -i "s|<version>.*</version> <!-- latest version -->|<version>${version}</version> <!-- latest version -->|" "$1"/README.md
