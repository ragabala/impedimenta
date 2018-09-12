#!/usr/bin/env bash
# coding=utf-8
#
# Install data/ml-latest-small.zip.
set -euo pipefail

readonly src="$(
    realpath --canonicalize-missing ~/.cache/average-rating/ml-latest-small.zip
)"
readonly dst="$(realpath --canonicalize-missing "data/$(basename "${src}")")"

# TODO: Download into /tmp/..., then move to ~/.cache/..., thus preventing
# partial downloads.
if [ ! -e "${src}" ]; then
    mkdir -p "$(dirname "${src}")"
    curl -o "${src}" \
        http://files.grouplens.org/datasets/movielens/ml-latest-small.zip
fi
if [ ! -e "${dst}" ]; then
    install -Dm640 "${src}" "${dst}"
fi
