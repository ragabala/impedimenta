#!/usr/bin/env bash
# coding=utf-8
#
# If the filesystem has already been formatted, don't format it again. Doing so
# will give the namenode and datanode differing cluster IDs, which will prevent
# them from working together.
set -euo pipefail

export JAVA_HOME=/usr/lib/jvm/default
export HADOOP_CONF_DIR="$(realpath --canonicalize-existing configs)"

~/data/hadoop-2.9.1/bin/hdfs namenode -format
