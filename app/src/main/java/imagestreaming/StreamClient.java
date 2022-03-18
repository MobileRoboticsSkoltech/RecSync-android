package imagestreaming;

import android.graphics.YuvImage;
import android.util.Log;

import com.googleresearch.capturesync.MainActivity;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


/**
 * Responsible for
 */
public class StreamClient extends Thread {
    public static final String TAG = "StreamClient";

    Socket clientSocket;
    InetAddress address;
    FileTransferUtils utils;
    public static final int PORT = 6969;
    private final ExecutorService frameProcessor = Executors.newSingleThreadExecutor();

    public StreamClient(InetAddress address, MainActivity context) throws IOException {
        // open socket connection
        this.address = address;
        utils = new FileTransferUtils(context);
    }

    public void onVideoFrame(File frame, long timestampNs) {
        // send frame over the channel
        if (!frameProcessor.isShutdown()) {
            frameProcessor.execute(
                    () -> {
                        try {
                            Log.d(TAG, "Sending frame");
                            // TODO: send frame timestamp

                            clientSocket = new Socket(address, 6969);
                            utils.sendFile(frame, clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Error sending frame");
                        }
                    }
            );
        }
    }


    public void closeConnection() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
