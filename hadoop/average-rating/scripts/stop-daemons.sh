#!/usr/bin/env bash
# coding=utf-8
source scripts/common.sh
~/data/hadoop-2.9.1/sbin/mr-jobhistory-daemon.sh stop historyserver
~/data/hadoop-2.9.1/sbin/stop-yarn.sh
~/data/hadoop-2.9.1/sbin/stop-dfs.sh
