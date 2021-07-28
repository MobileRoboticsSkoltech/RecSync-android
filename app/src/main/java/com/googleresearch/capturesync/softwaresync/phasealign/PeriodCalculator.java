package com.googleresearch.capturesync.softwaresync.phasealign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class PeriodCalculator {
    private final static long CALC_DURATION_MS = 10000;
    private volatile boolean shouldRegister;
    private ArrayList<Long> registeredTimestamps;

    public PeriodCalculator() {
        registeredTimestamps = new ArrayList<>();
    }

    // Blocking call, returns 0 in case of error
    public long getPeriodNs() throws InterruptedException {
        // Start recording timestamps
        registeredTimestamps = new ArrayList<>();
        shouldRegister = true;
        final CountDownLatch latch = new CountDownLatch(1);
        TimerTask task = new TimerTask() {
            public void run() {
                // Stop recording timestamps and calculate period
                shouldRegister = false;
                latch.countDown();
            }
        };
        Timer timer = new Timer("Timer");

        timer.schedule(task, CALC_DURATION_MS);
        latch.await();
        return calcPeriodNsClusters(getDiff(registeredTimestamps));
    }

    private ArrayList<Long> getDiff(ArrayList<Long> arrayList) {
        Long prev = 0L;
        ArrayList<Long> result = new ArrayList<>();
        for (Long aLong : arrayList) {
            if (prev == 0L) {
                prev = aLong;
            } else {
                result.add(aLong - prev);
                prev = aLong;
            }
        }
        return result;
    }

    private long calcPeriodNsClusters(ArrayList<Long> numArray) {
        long initEstimate = Collections.min(numArray);
        long nClust = Math.round(1.0 * Collections.max(numArray) / initEstimate);
        double weightedSum = 0L;
        for (int i = 0; i < nClust; i++) {
            int finalI = i;
            ArrayList<Long> clust = (ArrayList<Long>)numArray.stream().filter(
                    x -> (x > (finalI + 0.5)*initEstimate) && (x < (finalI + 1.5)*initEstimate)
            ).collect(Collectors.toList());
            if (clust.size() > 0) {
                weightedSum += 1.0 * median(clust) / (i + 1) * clust.size();
            }
        }
        return Math.round(weightedSum / numArray.size());
    }

    private long calcPeriodNsMedian(ArrayList<Long> numArray) {
        return median(numArray);
    }

    private long median(ArrayList<Long> numArray) {
        Collections.sort(numArray);
        int middle = numArray.size() / 2;
        middle = middle > 0 && middle % 2 == 0 ? middle - 1 : middle;
        return numArray.get(middle);
    }

    public void onFrameTimestamp(long timestampNs) {
        // Register timestamp
        if (shouldRegister) {
            registeredTimestamps.add(timestampNs);
        }
    }
}
