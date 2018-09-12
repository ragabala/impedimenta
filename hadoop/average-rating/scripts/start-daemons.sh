#!/usr/bin/env bash
# coding=utf-8
make data/hadoop-2.9.1

source scripts/hadoop-env.sh
data/hadoop-2.9.1/sbin/start-dfs.sh
data/hadoop-2.9.1/sbin/start-yarn.sh
data/hadoop-2.9.1/sbin/mr-jobhistory-daemon.sh start historyserver
