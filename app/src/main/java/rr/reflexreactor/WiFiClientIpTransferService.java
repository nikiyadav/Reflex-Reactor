package rr.reflexreactor;

/**
 * Created by root on 6/2/16.
 */

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class WiFiClientIpTransferService extends IntentService {

    public WiFiClientIpTransferService(String name) {
        super(name);
    }
    public WiFiClientIpTransferService() {
        super("WiFiClientIPTransferService");
    }

    Handler mHandler;

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = GlobalApplication.getGlobalAppContext();
        if (intent.getAction().equals(FileTransferService.ACTION_SEND_FILE)) {
            String host = intent.getExtras().getString(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS);
            String InetAddress =  intent.getExtras().getString(FileTransferService.inetaddress);

            Socket socket = new Socket();
            int port = intent.getExtras().getInt(FileTransferService.EXTRAS_GROUP_OWNER_PORT);

            try {
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), FileTransferService.SOCKET_TIMEOUT);
                OutputStream stream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream is = null;

               /*
                * Object that is used to send file name with extension and received on other side.
                */
                ObjectOutputStream oos = new ObjectOutputStream(stream);
                WifiTransferModel transObj = new WifiTransferModel(InetAddress);

                oos.writeObject(transObj);
                System.out.println("Sending request to Socket Server");

                oos.close();//close the ObjectOutputStream after sending data.
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

}
