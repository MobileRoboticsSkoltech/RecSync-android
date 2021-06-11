# RecSync Android

Andrioid application which allows you to record synchronized smarpthone videos with sub-millisecond accuracy.

## Usage:
- TODO

## Our contribution:

- Integrated **synchronized video recording**
- Scripts for extraction, alignment and processing of video frames
- Experiment with flash blinking to evaluate video frames synchronization accuracy
- Panoramic video demo with automated Hujin stitching

## Panoramic demo

- We provide scripts to **stitch 2 syncronized smatphone videos** with Hujin panorama CLI tools
- Usage:
    - Run ```./make_demo.sh {VIDEO_LEFT} {VIDEO_RIGHT}```

## This work is based on "Wireless Software Synchronization of Multiple Distributed Cameras"

Reference code for the paper
[Wireless Software Synchronization of Multiple Distributed Cameras](https://arxiv.org/abs/1812.09366).
_Sameer Ansari, Neal Wadhwa, Rahul Garg, Jiawen Chen_, ICCP 2019.
