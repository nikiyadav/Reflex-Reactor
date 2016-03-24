package rr.reflexreactor;

// Copyright 2011 Google Inc. All Rights Reserved.

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

    Handler mHandler;

    public static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    public static  int PORT = 8888;
    public static final String inetaddress = "inetaddress";
    public static final int ByteSize = 512;
    public static final String Extension = "extension";
    public static final String Filelength = "filelength";
    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            String extension = intent.getExtras().getString(Extension);
            String filelength = intent.getExtras().getString(Filelength);

            try {
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                OutputStream stream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream is = null;

                /*
                 * Object that is used to send file name with extension and recieved on other side.
                 */
                Long FileLength = Long.parseLong(filelength);
                WifiTransferModel transObj = null;
                ObjectOutputStream oos = new ObjectOutputStream(stream);
                if(transObj == null) transObj = new WifiTransferModel();


                transObj = new WifiTransferModel(extension,FileLength,"");
                oos.writeObject(transObj);

                try {
                    is = cr.openInputStream(Uri.parse(fileUri));
                }
                catch (FileNotFoundException e) {
                }
                DeviceDetailFragment.copyFile(is, stream);
                stream.flush();
                oos.close();   //close the ObjectOutputStream after sending data.
            }
            catch (IOException e) {
                e.printStackTrace();
                mHandler.post(new Runnable() {

                    public void run() {
                        Toast.makeText(FileTransferService.this, "Paired Device is not Ready to receive the file", Toast.LENGTH_LONG).show();
                    }
                });
                DeviceDetailFragment.DismissProgressDialog();
                try {
                    throw e;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }
}