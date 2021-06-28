## RecSync Android: Sub-millisecond Video Synchronization of Multiple Android Smartphones

Andrioid application which allows you to record synchronized smarpthone videos with sub-millisecond accuracy.

### Usage:


#### Leader smartphone setup

1.  Start a Wi-Fi hotspot.
2.  The app should display connected clients and buttons for recording control

#### Client smartphones setup

1.  Enable WiFi and connect to the Wi-Fi hotspot.

#### Capturing images

1.  [Optional step] Press the ```calculate period``` button. The app will analyze frame stream and use the calculated frame period in further synchronization steps.
2.  Press the ```phase align``` button.
3.  Adjust exposure and ISO to your needs.
4.  Press the ```record video``` button to start synchronized video recording.

### Our contribution:

- Integrated **synchronized video recording**
- Scripts for extraction, alignment and processing of video frames
- Experiment with flash blinking to evaluate video frames synchronization accuracy
- Panoramic video demo with automated Hujin stitching

### Panoramic video stitching demo

### [Link to youtube demo video](https://youtu.be/W6iANtCuQ-o)

- We provide scripts to **stitch 2 syncronized smatphone videos** with Hujin panorama CLI tools
- Usage:
    - Run ```./make_demo.sh {VIDEO_LEFT} {VIDEO_RIGHT}```

### This work is based on "Wireless Software Synchronization of Multiple Distributed Cameras"

Reference code for the paper
[Wireless Software Synchronization of Multiple Distributed Cameras](https://arxiv.org/abs/1812.09366).
_Sameer Ansari, Neal Wadhwa, Rahul Garg, Jiawen Chen_, ICCP 2019.
