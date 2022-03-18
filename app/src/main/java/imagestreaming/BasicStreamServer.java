package imagestreaming;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Receives images sent from the client smartphone
 * with BasicStreamClient
 */
public class BasicStreamServer extends StreamServer {
    public static final String TAG = "StreamServer";
    private static final int SOCKET_WAIT_TIME_MS = 1000;
    private static final int PORT = 6969;
    private final FileTransferUtils utils;
    private static final String tmpFilename = "tmp_frame.jpg";
    private volatile boolean mIsExecuting;

    public BasicStreamServer(FileTransferUtils utils) {
        this.utils = utils;
    }

    @Override
    public void run() {
        mIsExecuting = true;

        Log.d(TAG, "waiting to accept connection from client...");
        try (
                ServerSocket rpcSocket = new ServerSocket(PORT)
        ) {
            rpcSocket.setReuseAddress(true);
            rpcSocket.setSoTimeout(SOCKET_WAIT_TIME_MS);
            while (mIsExecuting) {
                try (
                        Socket clientSocket = rpcSocket.accept()
                ) {
                    clientSocket.setKeepAlive(true);

                    Log.d(TAG, "accepted connection from client");

                    // receive frame
                    utils.receiveFile(tmpFilename, clientSocket);
                    Log.d(TAG, "File received");

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

