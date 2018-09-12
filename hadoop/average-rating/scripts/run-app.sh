#!/usr/bin/env bash
# coding=utf-8
set -euo pipefail

make data/hadoop-3.1.1

source scripts/hadoop-env.sh
data/hadoop-3.1.1/bin/hadoop \
    jar target/average-rating-1-SNAPSHOT.jar \
    name.ichimonji10.app.AverageRating \
    /ml-latest-small/ratings.csv \
    /output
data/hadoop-3.1.1/bin/hadoop fs -cat '/output/*'
