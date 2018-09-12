#!/usr/bin/env bash
# coding=utf-8
set -euo pipefail

source scripts/common.sh
readonly src='data/ml-latest-small'
make "${src}"
if ~/data/hadoop-2.9.1/bin/hadoop fs -ls "/$(basename "${src}")" >/dev/null; then
    echo 'FS already populated.'
else
    echo 'Populating FS'
    ~/data/hadoop-2.9.1/bin/hadoop fs -put "${src}" /
fi
