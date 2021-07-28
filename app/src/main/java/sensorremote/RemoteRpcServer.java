package sensorremote;


import android.app.AlertDialog;
import android.util.Log;

import com.googleresearch.capturesync.MainActivity;
import com.googleresearch.capturesync.SoftwareSyncController;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * OpenCamera Sensors server v. 0.0
 * <p>
 * Accepted message types:
 * - get IMU (accelerometer/gyroscope)
 * - start/stop video
 * Response structure:
 * - 1st line: SUCCESS/ERROR message
 * - 2nd line: version string
 * - the rest: request response
 */
public class RemoteRpcServer extends Thread {
    private static final String TAG = "RemoteRpcServer";
    private static final int SOCKET_WAIT_TIME_MS = 1000;
    private final Properties mConfig;
    private final RemoteRpcRequestHandler mRequestHandler;
    private final MainActivity mContext;
    private volatile boolean mIsExecuting;

    public RemoteRpcServer(MainActivity context) throws IOException {
        mContext = context;
        mConfig = RemoteRpcConfig.getProperties(context);
        mRequestHandler = new RemoteRpcRequestHandler(context);

        Log.d(TAG, "Hostname " + getIPAddress());

    }

    /**
     * Finds this devices's IPv4 address that is not localhost and not on a dummy interface.
     *
     * @return the String IP address on success.
     * @throws SocketException on failure to find a suitable IP address.
     */
    public static InetAddress getIPAddress() throws SocketException {
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface intf : interfaces) {
            for (InetAddress addr : Collections.list(intf.getInetAddresses())) {
                if (!addr.isLoopbackAddress()
                        && !intf.getName().equals("dummy0")
                        && addr instanceof Inet4Address) {
                    return addr;
                }
            }
        }
        throw new SocketException("No viable IP Network addresses found.");
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

    private void handleRequest(String msg, PrintStream outputStream, BufferedOutputStream outputByte) {
        if (msg.equals(mConfig.getProperty("VIDEO_START_REQUEST"))) {
            outputStream.println(
                    mRequestHandler.handleVideoStartRequest()
            );
        } else if (msg.equals(mConfig.getProperty("VIDEO_STOP_REQUEST"))) {
            outputStream.println(
                    mRequestHandler.handleVideoStopRequest()
            );
        } else if (msg.equals(mConfig.getProperty("GET_VIDEO_REQUEST"))) {
            mRequestHandler.handleVideoGetRequest(outputStream);
        } else if (msg.equals(mConfig.getProperty("GET_HOSTNAMES_REQUEST"))) {
            outputStream.println(
                    mRequestHandler.handleClientsIpRequest()
            );
        } else if (msg.equals(mConfig.getProperty("GET_TS_FILE_REQUEST"))) {
            outputStream.println(
                    mRequestHandler.handleTsFileRequest()
            );
        } else {
            outputStream.println(
                    mRequestHandler.handleInvalidRequest()
            );
        }
    }

    @Override
    public void run() {
        // TODO: report hostname some other way
        SoftwareSyncController controller = mContext.getSoftwareSyncController();
        if (controller.isLeader()) {
            mContext.runOnUiThread(
                    () -> {
                        try {
                            new AlertDialog.Builder(mContext)
                                    .setTitle("Smartphone hostname")
                                    .setMessage("Hostname: " + getIPAddress())

                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }

                    }
            );
        }

        mIsExecuting = true;
        Log.d(TAG, "waiting to accept connection from client...");

        try (
                ServerSocket rpcSocket = new ServerSocket(Integer.parseInt(mConfig.getProperty("RPC_PORT")))
        ) {
            rpcSocket.setReuseAddress(true);
            rpcSocket.setSoTimeout(SOCKET_WAIT_TIME_MS);
            while (mIsExecuting) {
                try (
                        Socket clientSocket = rpcSocket.accept()
                ) {
                    clientSocket.setKeepAlive(true);
                    Log.d(TAG, "accepted connection from client");
                    try (
                            InputStream inputStream = clientSocket.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            PrintStream outputStream = new PrintStream(clientSocket.getOutputStream());
                            BufferedOutputStream outputByte = new BufferedOutputStream(clientSocket.getOutputStream())
                    ) {

                        String inputLine;
                        while (mIsExecuting && !clientSocket.isClosed() && (inputLine = reader.readLine()) != null) {
                            // Received new request from the client
                            handleRequest(inputLine, outputStream, outputByte);
                            outputStream.flush();
                        }
                    }
                    Log.d(TAG, "closing connection to client");


                } catch (IOException e) {
                   /* if (MyDebug.LOG) {
                        Log.d(TAG, "socket timed out, waiting for new connection to client");
                    }*/
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
