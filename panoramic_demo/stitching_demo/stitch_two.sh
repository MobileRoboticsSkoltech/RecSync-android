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

set -evo pipefail

IMAGE_1=$1
IMAGE_2=$2
# TODO fix this
NAME=$3
PROJECT='./stitching_demo/project.pto'
echo "$IMAGE_1" - "$IMAGE_2"
pto_gen --projection=0 --fov=50 -o "$PROJECT" "$IMAGE_1" "$IMAGE_2"
pto_lensstack --new-lens i0,i1 -o "$PROJECT" "$PROJECT"
cd "./stitching_demo" || exit
PROJECT='project.pto'
# cpfind -o "$PROJECT" --multirow --celeste "$PROJECT"
# celeste_standalone -i project.pto -o project.pto
# cpclean -o "$PROJECT" "$PROJECT"
# linefind -o "$PROJECT" "$PROJECT"
# --opt=v,a,b,c,d,e,g,t,y,p,r,TrX,TrY,TrZ,Tpy,Tpp
pto_var --anchor=1 --opt=Vb0 --set=y0=37.086,p0=-0.295,r0=3.013,y1=0,p1=0,r1=0,TrX=0,TrY=0,TrZ=0,Tpy=0,Tpp=0 -o "$PROJECT" "$PROJECT"
autooptimiser -n -o "$PROJECT" "$PROJECT"
pano_modify --crop=878,3000,39,710 --canvas=3000x750 --fov=120x30 -o "$PROJECT" "$PROJECT"

vig_optimize -o "$PROJECT" "$PROJECT"
hugin_executor --stitching --prefix="$NAME" "$PROJECT"