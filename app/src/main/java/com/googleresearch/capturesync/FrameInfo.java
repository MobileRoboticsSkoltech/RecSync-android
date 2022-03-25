package com.googleresearch.capturesync;

import java.io.File;
import java.util.ArrayDeque;

public interface FrameInfo {
    public ArrayDeque<Long> getLatestFrames();

    public void displayStreamFrame(File streamFrame);
}
