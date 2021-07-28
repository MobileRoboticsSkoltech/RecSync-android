package sensorremote;

import android.util.Log;

import com.googleresearch.capturesync.MainActivity;
import com.googleresearch.capturesync.SoftwareSyncController;
import com.googleresearch.capturesync.softwaresync.ClientInfo;
import com.googleresearch.capturesync.softwaresync.SoftwareSyncLeader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RemoteRpcRequestHandler {
    public static final String TAG = "RequestHandler";
    public static final String SENSOR_DATA_END_MARKER = "sensor_end";
    // Set to some adequate time which is likely more than phase report time
    // TODO: we could set it dynamically using current PHASE_CALC_N_FRAMES
    public static final long PHASE_POLL_TIMEOUT_MS = 10_000;
    private static final int BUFFER_SIZE = 1024;
    private final MainActivity mContext;
    private final RemoteRpcResponse.Builder mResponseBuilder;

    RemoteRpcRequestHandler(MainActivity context) {
        mContext = context;
        mResponseBuilder = new RemoteRpcResponse.Builder(context);
    }

    private String getTsData(File tsFile) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append(tsFile.getName())
                .append("\n");
        try (BufferedReader br = new BufferedReader(new FileReader(tsFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                msg.append(line)
                        .append("\n");
            }
        }
        return msg.toString();
    }

    RemoteRpcResponse handleInvalidRequest() {
        return mResponseBuilder.error("Invalid request", mContext);
    }

    RemoteRpcResponse handleVideoStartRequest() {
//        // Start video recording
//        Preview preview = mContext.getPreview();
//
//        Callable<Void> recStartCallable = () -> {
//            // Making sure video is switched on
//            if (!preview.isVideo()) {
//                preview.switchVideo(false, true);
//            }
//            // In video mode this means "start video"
//            mContext.takePicture(false);
//            return null;
//        };
//
//        // Await recording start
//        FutureTask<Void> recStartTask = new FutureTask<>(recStartCallable);
//        mContext.runOnUiThread(recStartTask);
//
//        // Await video phase event
//        BlockingQueue<VideoPhaseInfo> videoPhaseInfoReporter = preview.getVideoPhaseInfoReporter();
//        if (videoPhaseInfoReporter != null) {
//            VideoPhaseInfo phaseInfo;
//            try {
//                phaseInfo = videoPhaseInfoReporter
//                        .poll(PHASE_POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
//                if (phaseInfo != null) {
//                    return mResponseBuilder.success(phaseInfo.toString(), mContext);
//                } else {
//                    return mResponseBuilder.error("Failed to retrieve phase info, reached poll limit", mContext);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return mResponseBuilder.error("Failed to retrieve phase info", mContext);
//            }
//        } else {
//            if (MyDebug.LOG) {
//                Log.d(TAG, "Video frame info wasn't initialized, failed to retrieve phase info");
//            }
//            return mResponseBuilder.error("Video frame info wasn't initialized, failed to retrieve phase info", mContext);
//        }
        return mResponseBuilder.error("Unimplemented", mContext);
    }

    RemoteRpcResponse handleVideoStopRequest() {
//        mContext.runOnUiThread(
//                () -> {
//                    Preview preview = mContext.getPreview();
//                    if (preview.isVideo() && preview.isVideoRecording()) {
//                        preview.stopVideo(false);
//                    }
//                }
//        );
//        return mResponseBuilder.success("", mContext);
        return mResponseBuilder.error("Unimplemented", mContext);
    }

    //
    void handleVideoGetRequest(PrintStream outputStream) {
//        Preview preview = mContext.getPreview();
//        BlockingQueue<String> videoReporter;
//        if (preview != null &&
//            (videoReporter = preview.getVideoAvailableReporter()) != null) {
        try {
//                // await available video file
//                if (preview.isVideoRecording()) {
//                    videoReporter.take();
//                }
//                // get file
//                ExtendedAppInterface appInterface = mContext.getApplicationInterface();
            File videoFile = new File(mContext.getLastVideoPath());
            boolean canRead = videoFile.canRead();
            Log.d(TAG, "Can read video file: " + canRead);
            if (videoFile.canRead()) {
                // Transfer file size in bytes and filename
                outputStream.println(mResponseBuilder.success(
                        videoFile.length() + "\n" + videoFile.getName() + "\n",
                        mContext
                ));
                outputStream.flush();
                // Transfer file bytes
                FileInputStream inputStream = new FileInputStream(videoFile);

                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
                inputStream.close();
            } else {
                outputStream.println(mResponseBuilder.error("Couldn't get last video file data", mContext));
            }
        } catch (IOException e) {
            e.printStackTrace();
            outputStream.println(mResponseBuilder.error("Error getting video file", mContext));
        }
//        } else {
//            outputStream.println(
//                    mResponseBuilder.error("Null reference", mContext)
//            );
//        }
    }

    RemoteRpcResponse handleTsFileRequest() {
        StringBuilder timestamps = new StringBuilder();
        File tsFile = new File(mContext.getLastTsPath());
        boolean canRead = tsFile.canRead();
        Log.d(TAG, "Can read video file: " + canRead);
        if (tsFile.canRead()) {
            try {
                return mResponseBuilder.success(getTsData(tsFile), mContext);
            } catch (IOException e) {
                e.printStackTrace();
                return mResponseBuilder.error("Couldn't get last video file data", mContext);
            }
        } else {
            return mResponseBuilder.error("Couldn't get last video file data", mContext);
        }
    }

    RemoteRpcResponse handleClientsIpRequest() {
        StringBuilder hostnames = new StringBuilder();

        SoftwareSyncController controller = mContext.getSoftwareSyncController();
        if (controller.isLeader()) {
            SoftwareSyncLeader leader = (SoftwareSyncLeader) controller.getSoftwareSync();
            Map<InetAddress, ClientInfo> clients = leader.getClients();
            for (InetAddress inetAddress : clients.keySet()) {
                hostnames.append(inetAddress.getHostAddress()).append(",");
            }
            return mResponseBuilder.success(hostnames.toString(), mContext);
        } else {
            return mResponseBuilder.error("Cannot get ip of clients from client", mContext);
        }
    }
}
