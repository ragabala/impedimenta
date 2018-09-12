#!/usr/bin/env bash
# coding=utf-8
set -euo pipefail

source scripts/common.sh
~/data/hadoop-2.9.1/bin/hadoop \
    jar target/average-rating-1-SNAPSHOT.jar \
    name.ichimonji10.app.AverageRating \
    /ml-latest-small/ratings.csv \
    /output
~/data/hadoop-2.9.1/bin/hadoop fs -cat '/output/*'
