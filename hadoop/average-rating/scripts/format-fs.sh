#!/usr/bin/env bash
# coding=utf-8
#
# If the filesystem has already been formatted, don't format it again. Doing so
# will give the namenode and datanode differing cluster IDs, which will prevent
# them from working together.
set -euo pipefail

source scripts/common.sh
~/data/hadoop-2.9.1/bin/hdfs namenode -format
