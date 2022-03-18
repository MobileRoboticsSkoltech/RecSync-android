package imagestreaming;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Provides methods for file transfer with TCP sockets
 */
public class FileTransferUtils {
    private static final String TAG = "FileTransferUtils";
    private static final int BUFFER_SIZE = 2048;

    Context mContext;

    public FileTransferUtils(Context context) {
        mContext = context;
    }

    /**
     * Sends specified file with provided TCP socket
     * @param file
     * @param sendSocket
     * @throws IOException
     */
    // TODO: better exception handling
    public void sendFile(
            File file, Socket sendSocket
    ) throws IOException {
        byte[] data;
        Log.d(TAG, "Connected to Server...");
        data = new byte[BUFFER_SIZE];

        // Sending File Data
        Log.d(TAG, "Sending file data...");
        FileInputStream fileStream = new FileInputStream(file);
        BufferedInputStream fileBuffer = new BufferedInputStream(fileStream);
        OutputStream out = sendSocket.getOutputStream();
        int count;
        while ((count = fileBuffer.read(data)) > 0) {
            Log.d(TAG, "Data Sent : " + count);
            out.write(data, 0, count);
            out.flush();
        }
        out.close();
        fileBuffer.close();
        fileStream.close();
    }

    /**
     * Handles receiving file with specified TCP socket,
     * saves it
     * @throws IOException
     */
    public File receiveFile(String fileName, Socket receiveSocket) throws IOException {
        Log.d(TAG, "Now receiving file...");
        Log.d(TAG, "File Name : " + fileName);
        byte[] data = new byte[BUFFER_SIZE];
        File file = new File(mContext.getExternalFilesDir(null), fileName);
        FileOutputStream fileOut = new FileOutputStream(file);
        InputStream fileIn = receiveSocket.getInputStream();
        BufferedOutputStream fileBuffer = new BufferedOutputStream(fileOut);
        int count;
        int sum = 0;
        while ((count = fileIn.read(data)) > 0) {
            sum += count;
            fileBuffer.write(data, 0, count);
            Log.d(TAG, "Data received : " + sum);
            fileBuffer.flush();
        }
        Log.d(TAG, "File Received...");
        fileBuffer.close();
        fileIn.close();
        receiveSocket.close();

        return file;
    }
}