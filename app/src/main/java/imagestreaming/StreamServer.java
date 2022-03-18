package imagestreaming;

import android.util.Log;

import com.googleresearch.capturesync.MainActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * (Current simple implementation) Receives images with timestamps
 * probably - matches received image with a buffer of images to find THE pair !1!
 */
public class StreamServer extends Thread {
    private static final int SOCKET_WAIT_TIME_MS = 1000;
    private volatile boolean mIsExecuting;
    public static final String TAG = "StreamServer";
    private FileTransferUtils utils;

    public StreamServer(MainActivity context) {
        utils = new FileTransferUtils(context);
    }

    @Override
    public void run() {
        mIsExecuting = true;
        
        Log.d(TAG, "waiting to accept connection from client...");
        try (
                ServerSocket rpcSocket = new ServerSocket(6969)
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
                    utils.receiveFile("tmp_frame.jpg", clientSocket);
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

