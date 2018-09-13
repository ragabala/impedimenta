#!/usr/bin/env bash
# coding=utf-8
#
# Install data/ml-latest-small.
set -euo pipefail

readonly src_archive="$(
    realpath --canonicalize-missing ~/.cache/average-rating/ml-latest-small.zip
)"
readonly dst_dir="$(realpath --canonicalize-existing data)"

# TODO: Download into /tmp/..., then move to ~/.cache/..., thus preventing
# partial downloads.
if [ ! -e "${src_archive}" ]; then
    mkdir -p "$(dirname "${src_archive}")"
    curl -o "${src_archive}" \
        http://files.grouplens.org/datasets/movielens/ml-latest-small.zip
fi
unzip -d "${dst_dir}" "${src_archive}"
