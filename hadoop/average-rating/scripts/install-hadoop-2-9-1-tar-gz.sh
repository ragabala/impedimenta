#!/usr/bin/env bash
# coding=utf-8
#
# Install data/hadoop-2.9.1.tar.gz.
set -euo pipefail

readonly src="$(
    realpath --canonicalize-missing ~/.cache/average-rating/hadoop-2.9.1.tar.gz
)"
readonly dst="$(realpath --canonicalize-missing "data/$(basename "${src}")")"

# TODO: Download into /tmp/..., then move to ~/.cache/..., thus preventing
# partial downloads.
if [ ! -e "${src}" ]; then
    mkdir -p "$(dirname "${src}")"
    curl -o "${src}" \
        'https://www.apache.org/dyn/closer.cgi/hadoop/common/hadoop-2.9.1/hadoop-2.9.1-src.tar.gz'
fi
if [ ! -e "${dst}" ]; then
    install -D "${src}" "${dst}"
fi
