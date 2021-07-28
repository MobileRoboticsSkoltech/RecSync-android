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

import re
import os
ALLOWED_EXTENSIONS = ['.jpg', '.jpeg', '.npy', '.png', '.pcd']


def make_dir_if_needed(dir_path):
    if not os.path.exists(dir_path):
        os.makedirs(dir_path)


def get_timestamp_filename(timestamp, extension):
    return "%d.%s" % (timestamp.secs * 1e9 + timestamp.nsecs, extension)


def extract_frame_data(target_dir, video_path):
    # load frame timestamps csv, rename frames according to it
    video_root, video_filename = os.path.split(video_path)
    video_name, _ = os.path.splitext(video_filename)

    with open(os.path.join(video_root, video_name + ".csv"))\
            as frame_timestamps_file:
        filename_timestamps = list(map(
            lambda x: (x.strip('\n'), int(x)), frame_timestamps_file.readlines()
        ))
        length = len(list(filter(
            lambda x: os.path.splitext(x)[1] in ALLOWED_EXTENSIONS,
            os.listdir(target_dir)
        )))
        # frame number assertion
        # assert len(filename_timestamps) == len(list(filter(
        #     lambda x: os.path.splitext(x)[1] in ALLOWED_EXTENSIONS,
        #     os.listdir(target_dir)
        # ))), "Frame number in video %d and timestamp files %d did not match" % (l, len(filename_timestamps))

        _, extension = os.path.splitext(os.listdir(target_dir)[0])
        for i in range(length):
            timestamp = filename_timestamps[i]
            os.rename(
                os.path.join(target_dir, "frame-%d.png" % (i + 1)),
                os.path.join(target_dir, timestamp[0] + extension)
            )