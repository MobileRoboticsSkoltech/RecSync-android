#!/bin/bash
# Copyright 2021 Mobile Robotics Lab. at Skoltech
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -eo pipefail

SMARTPHONE_VIDEO_PATH=$1

DATA_DIR="output/"$2


## Create a subdirectory for extraction
rm -rf "$DATA_DIR"
mkdir -p "$DATA_DIR"

# SMARTPHONE_VIDEO_DIR="${SMARTPHONE_VIDEO_PATH%/*}"

# Check if video exists
echo "$SMARTPHONE_VIDEO_PATH"
if [ ! -f "$SMARTPHONE_VIDEO_PATH" ]; then
  >&2 echo "Provided smartphone video doesn't exist"
else
  DIR="$DATA_DIR"
  rm -rf "$DIR"
  mkdir  "$DIR"
  ffmpeg -i "$SMARTPHONE_VIDEO_PATH" -vsync 0 "$DIR/frame-%d.png"
  python extract.py --output "$DIR" \
  --type sm_frames --frame_dir "$DIR" --vid "$SMARTPHONE_VIDEO_PATH"
fi

