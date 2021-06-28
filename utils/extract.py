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

import argparse
from src.extraction_utils import extract_frame_data


def main():
    parser = argparse.ArgumentParser(
        description="Extracts frames"
    )
    parser.add_argument(
        "--output",
        required=True
    )
    parser.add_argument('--frame_dir',
                        help='<Optional> Smartphone frames directory')
    parser.add_argument('--vid', help='<Optional> Smartphone video path')

    args = parser.parse_args()
    # TODO: args assertion for dir and vid
    print("Extracting smartphone video frame data..")
    extract_frame_data(args.frame_dir, args.vid)


if __name__ == '__main__':
    main()
