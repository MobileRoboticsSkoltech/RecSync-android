package imagestreaming;

import android.util.Log;

import com.googleresearch.capturesync.FrameInfo;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Comparator;

public class ImageMatcher {
    private final FrameInfo frameInfo;
    private final static Long MATCHING_THRESHOLD = 5000000L;
    private final static String TAG = "ImageMatcher";

    public ImageMatcher(FrameInfo frameInfo) {
        this.frameInfo = frameInfo;
    }

    void onClientImageAvailable(File clientFrame) {
        // takes client frame with timestamp, finds a leader frame with a matching timestamp
        Long timestamp = Long.parseLong(clientFrame.getName().split("_", -1)[0]);

        ArrayDeque<Long> latestFrames = frameInfo.getLatestFrames();

        Long matchingTimestamp = latestFrames.stream().filter(
                leaderTimestamp -> leaderTimestamp - timestamp < MATCHING_THRESHOLD)
                .min(Comparator.comparingLong(leaderTimestamp -> Math.abs(leaderTimestamp - timestamp))
                ).orElse(null);

        if (matchingTimestamp != null) {
            Log.d(TAG, "Found match for the client frame: " + matchingTimestamp + " " + timestamp);
        } else {
            Log.d(TAG, "Match not found");
            Log.d(TAG, "Client: " + timestamp);
            Log.d(TAG, "Leader ts: " + latestFrames.toString());
        }

        frameInfo.displayStreamFrame(clientFrame);
        // reports image pair to the depth estimator?

    }
}
