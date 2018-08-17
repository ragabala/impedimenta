#!/usr/bin/env sh
# Create gource videos of a repository.
#
# Repository exists at $repo_dir.
# Files such as ${prefix}.ppm, ${prefix}.mkv, and ${prefix}.webm are created.
# Files are created in $work_dir.
# Files are moved to $dest_dir upon finish.

# Die if an uninitialized var is used.
set -u
# Die if any command returns non-zero.
set -e

# SET THESE VARS!
repo_dir=
work_dir=
dest_dir=
prefix=$(basename "$repo_dir")

#-------------------------------------------------------------------------------

cd "$work_dir"

# Create uncompressed PPM file. ffmpeg uses this file as input.
# Gource requires a video display, so create a virtual one with xvfb-run.
echo Creating PPM file.
set +e
xvfb-run \
    --auto-servernum \
    --server-args "-screen 0 1280x720x24" \
    gource \
    -1280x720 \
    --seconds-per-day 0.5 \
    --key \
    --output-framerate 30 \
    --output-ppm-stream "${prefix}.ppm" \
    "$repo_dir"
set -e

# create mkv video
echo Creating mkv video.
ffmpeg \
    -y -r 30 -f image2pipe -codec:v ppm -i "${prefix}.ppm" \
    -codec:v libx264 \
    -preset fast \
    -pix_fmt yuv420p \
    -crf 1 \
    -threads 0 \
    -bf 0 \
    "${prefix}.mkv"

# create webm video
#
# `-b:v NNN` is bitrate of video.
# NNN = 200kB is low quality and consumes 11-12 MB/min.
echo Creating webm video.
ffmpeg \
    -y -r 30 -f image2pipe -codec:v ppm -i "${prefix}.ppm" \
    -codec:v libvpx \
    -b:v 200kB \
    "${prefix}.webm"

mv -t "$dest_dir" "${prefix}.ppm" "${prefix}.mkv" "${prefix}.webm"
