#!/usr/bin/env bash

#
# Copyright 2022 Blue Circle Software, LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#

set -euo pipefail
set -x

if [[ -n "$(git status --porcelain)" ]]; then
  echo "Working tree is dirty. Commit or stash changes before releasing." >&2
  exit 1
fi

version="$(grep -m1 -E "<version>" pom.xml | sed -E "s/.*<version>([^<]+).*/\\1/")"
if [[ -z "${version}" ]]; then
  echo "Unable to determine version from pom.xml" >&2
  exit 1
fi

release_version="${version%-SNAPSHOT}"

scripts/update-readme-version.sh "$(pwd)"

if [[ -n "$(git status --porcelain README.md)" ]]; then
  git add README.md
  git commit -m "Update README for release ${release_version}"
fi

./mvnw release:prepare
./mvnw release:perform
./mvnw release:clean
