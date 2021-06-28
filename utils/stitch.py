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

import pandas as pd
import argparse
import os
import subprocess


def main():
    parser = argparse.ArgumentParser(
        description="Stitch multiple images"
    )

    parser.add_argument(
        "--matcher",
        required=True
    )
    parser.add_argument(
        "--target",
        required=True
    )
    args = parser.parse_args()

    target = args.target
    matcher = args.matcher
    stitch(target, matcher)


def stitch(target, matcher):
    with open(matcher, 'r') as csvfile:
        print(csvfile.name)
        df = pd.read_csv(csvfile)
        # TODO: change to csv lib usage instead of pandas
        for index, row in df.iterrows():
            # Read matching from csv
            print(row)
            right = row['right']
            left = row['left']
            right_img = os.path.join(target, '1', f'{right}.png')
            left_img = os.path.join(target, '2', f'{left}.png')
            print(f"Stitching {left_img} and {right_img}")
            # Launch the script for stitching two images, save w first name
            bashCommand = f" ./stitching_demo/stitch_two.sh {left_img} \
                {right_img} {left}"

            p = subprocess.Popen(bashCommand, shell=True)

            # and you can block util the cmd execute finish
            p.wait()


if __name__ == '__main__':
    main()
