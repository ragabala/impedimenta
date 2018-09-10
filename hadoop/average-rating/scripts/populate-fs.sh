#!/usr/bin/env bash
# coding=utf-8
set -euo pipefail

export JAVA_HOME=/usr/lib/jvm/default
export HADOOP_CONF_DIR="$(realpath --canonicalize-existing configs)"

readonly src='data/ml-latest-small'
make "${src}"
if ~/data/hadoop-2.9.1/bin/hadoop fs -ls "/$(basename "${src}")" >/dev/null; then
    echo 'FS already populated.'
else
    echo 'Populating FS'
    ~/data/hadoop-2.9.1/bin/hadoop fs -put "${src}" /
fi
