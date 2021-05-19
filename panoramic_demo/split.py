# Copyright 2020 Mobile Robotics Lab. at Skoltech
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

import argparse
import os
from shutil import copyfile
from src.alignment_utils import ALLOWED_EXTENSIONS
from src.rosbag_extraction_utils import make_dir_if_needed


def main():
    parser = argparse.ArgumentParser(
        description="Split extracted data"
    )
    parser.add_argument(
        "--target_dir",
        required=True
    )
    parser.add_argument(
        "--data_dir",
        required=True
    )
    parser.add_argument('--timestamps', nargs='+', help='<Optional> List of sequence timestamps')
    args = parser.parse_args()
    split(args.target_dir, args.data_dir, list(map(lambda x: int(x), args.timestamps)))
    

def split(target_dir, data_dir, timestamps):
    print("Splitting sequences...")

    filename_timestamps = list(map(
            lambda x: (x, int(os.path.splitext(x)[0])),
            filter(
                lambda x: os.path.splitext(x)[1] in ALLOWED_EXTENSIONS,
                os.listdir(target_dir)
            )
        ))
    filename_timestamps.sort(key=lambda tup: tup[1])
    sequences = []
    prev = 0
    for timestamp in timestamps:
        sequences.append(list(filter(lambda x: x[1] < timestamp and x[1] >= prev, filename_timestamps)))
        prev = timestamp
    sequences.append(list(filter(lambda x: x[1] >= timestamp, filename_timestamps)))
    for i, seq in enumerate(sequences):
        print("Copying sequence %d..." % i)
        new_dir = os.path.join(data_dir, "seq_%d" % i, os.path.split(target_dir)[-1])
        make_dir_if_needed(new_dir)
        for filename, _ in seq:
            copyfile(os.path.join(target_dir, filename), os.path.join(new_dir, filename))


if __name__ == '__main__':
    main()
