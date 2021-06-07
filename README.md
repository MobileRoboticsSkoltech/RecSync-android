# MROB Video modification [in progress]

## Main ideas:
    - Triggering at some time in the future is no longer needed(?)
    - New types of RPCs: start and stop video
    - ... profit!

## Our contribution:

    - Integrated synchronized video recording
    - Scripts for extraction, alignment and processing of video frames [in progress]

## Panoramic demo

- We provide scripts to **stitch 2 syncronized smatphone videos** with Hujin panorama CLI tools
- Usage:
    - Run ```./make_demo.sh {VIDEO_LEFT} {VIDEO_RIGHT}```

## This work is based on "Wireless Software Synchronization of Multiple Distributed Cameras"

Reference code for the paper
[Wireless Software Synchronization of Multiple Distributed Cameras](https://arxiv.org/abs/1812.09366).
_Sameer Ansari, Neal Wadhwa, Rahul Garg, Jiawen Chen_, ICCP 2019.
