package imagestreaming;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * A basic "streaming" client implementation sending images
 * over sockets
 */
public class BasicStreamClient extends StreamClient {
    public static final String TAG = "StreamClient";
    public static final int PORT = 6969;
    private final ExecutorService frameProcessor = Executors.newSingleThreadExecutor();
    Socket clientSocket;
    InetAddress address;
    FileTransferUtils utils;

    public BasicStreamClient(InetAddress address, FileTransferUtils utils) throws IOException {
        // open socket connection
        this.address = address;
        this.utils = utils;
    }

    public void onVideoFrame(File frame, long timestampNs) {
        // send frame over the channel
        if (!frameProcessor.isShutdown()) {
            frameProcessor.execute(
                    () -> {
                        try {
                            Log.d(TAG, "Sending frame");
                            // TODO: send frame timestamp

                            clientSocket = new Socket(address, PORT);
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
