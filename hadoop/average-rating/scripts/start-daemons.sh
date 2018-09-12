#!/usr/bin/env bash
# coding=utf-8
source scripts/common.sh
~/data/hadoop-2.9.1/sbin/start-dfs.sh
~/data/hadoop-2.9.1/sbin/start-yarn.sh
~/data/hadoop-2.9.1/sbin/mr-jobhistory-daemon.sh start historyserver
