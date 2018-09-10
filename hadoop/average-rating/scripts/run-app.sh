#!/usr/bin/env bash
# coding=utf-8
set -euo pipefail

export JAVA_HOME=/usr/lib/jvm/default
export HADOOP_CONF_DIR="$(realpath --canonicalize-existing configs)"

~/data/hadoop-2.9.1/bin/hadoop \
    jar target/average-rating-1-SNAPSHOT.jar \
    name.ichimonji10.app.AverageRating \
    /ml-latest-small/ratings.csv \
    /output
~/data/hadoop-2.9.1/bin/hadoop fs -cat '/output/*'
