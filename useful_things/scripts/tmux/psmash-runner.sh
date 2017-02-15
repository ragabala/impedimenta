#!/usr/bin/env bash
#
# Configure a tmux window. Make it so Pulp Smash can test several Pulp systems
# at once.
set -euo pipefail

# create panes
tmux split-window -v
tmux select-layout even-vertical
tmux split-window -v
tmux select-layout even-vertical
tmux split-window -v
tmux select-layout even-vertical
tmux split-window -v
tmux select-layout even-vertical
tmux split-window -v
tmux select-layout even-vertical
tmux split-window -v
tmux select-layout even-vertical

# configure each pane
tmux send-keys -t 0 'export PULP_SMASH_CONFIG_FILE=fedora-24-pulp-2-12.json' C-m
tmux send-keys -t 1 'export PULP_SMASH_CONFIG_FILE=fedora-24-pulp-2-13.json' C-m
tmux send-keys -t 2 'export PULP_SMASH_CONFIG_FILE=fedora-25-pulp-2-12.json' C-m
tmux send-keys -t 3 'export PULP_SMASH_CONFIG_FILE=fedora-25-pulp-2-13.json' C-m
tmux send-keys -t 4 'export PULP_SMASH_CONFIG_FILE=rhel-6-8-pulp-2-12.json' C-m
tmux send-keys -t 5 'export PULP_SMASH_CONFIG_FILE=rhel-7-3-pulp-2-12.json' C-m
tmux send-keys -t 6 'export PULP_SMASH_CONFIG_FILE=rhel-7-3-pulp-2-13.json' C-m

# etc
tmux set-window-option synchronize-panes on
tmux send-keys 'source ~/.venvs/pulp-smash/bin/activate' C-m
