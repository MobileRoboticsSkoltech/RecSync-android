package imagestreaming;

import java.io.File;

public abstract class StreamClient {
    public abstract void onVideoFrame(File frame, long timestampNs);

    public abstract void closeConnection();
}
