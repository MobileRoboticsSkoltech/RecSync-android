package imagestreaming;

import android.media.Image;
import android.os.Environment;
import android.util.Log;

import com.googleresearch.capturesync.FrameInfo;
import com.googleresearch.capturesync.MainActivity;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Receives images sent from the client smartphone
 * with BasicStreamClient
 */
public class BasicStreamServer extends StreamServer {
    public static final String TAG = "StreamServer";
    private static final int SOCKET_WAIT_TIME_MS = 1000;
    private static final int PORT = 6969;
    private final FileTransferUtils utils;
    private static final String tmpPath = "clientFrames";
    private volatile boolean mIsExecuting;
    private final ImageMatcher imageMatcher;
    public BasicStreamServer(FileTransferUtils utils, FrameInfo frameInfo) {
        this.utils = utils;
        this.imageMatcher = new ImageMatcher(frameInfo);
    }

    @Override
    public void run() {
        mIsExecuting = true;

        Log.d(TAG, "waiting to accept connection from client...");
        try (
                ServerSocket rpcSocket = new ServerSocket(PORT)
        ) {
            File sdcard = Environment.getExternalStorageDirectory();
            Path outputDir = Files.createDirectories(Paths.get(sdcard.getAbsolutePath(), MainActivity.SUBDIR_NAME, tmpPath));

            rpcSocket.setReuseAddress(true);
            rpcSocket.setSoTimeout(SOCKET_WAIT_TIME_MS);
            while (mIsExecuting) {
                try (
                        Socket clientSocket = rpcSocket.accept()
                ) {
                    clientSocket.setKeepAlive(true);

                    Log.d(TAG, "accepted connection from client");

                    // receive frame
                    File clientFrame = utils.receiveFile(outputDir.toString(), clientSocket);
                    Log.d(TAG, "File received");

                    imageMatcher.onClientImageAvailable(clientFrame);
                } catch (IOException e) {
                    Log.d(TAG, "socket timed out, waiting for new connection to client");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isExecuting() {
        return mIsExecuting;
    }

    /**
     * Safe to call even when not executing
     */
    public void stopExecuting() {
        mIsExecuting = false;
    }
}

