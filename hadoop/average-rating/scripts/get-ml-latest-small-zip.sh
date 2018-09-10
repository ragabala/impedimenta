#!/usr/bin/env bash
# coding=utf-8
set -euo pipefail

if [ -f ~/data/ml-latest-small.zip ]; then
    install -D ~/data/ml-latest-small.zip "$1"
else
    mkdir -p "$(dirname "$1")"
    curl -o "$1" \
        http://files.grouplens.org/datasets/movielens/ml-latest-small.zip
fi
