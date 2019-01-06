#!/usr/bin/env bash

set -ex

# When doing a release, update the README with the latest version numbers
# Parameter 1: path to project root

tmpfile=/tmp/update-readme-version.$$

trap "rm -f ${tmpfile}" EXIT

version=`cat $1/release.properties | egrep "^scm.tag=" | sed "s/^scm.tag=json-interface-generator-//"`
cat $1/README.md | sed "s|<version>.*</version> <!-- latest version -->|<version>${version}</version> <!-- latest version -->|" > ${tmpfile}
cat ${tmpfile} > $1/README.md
