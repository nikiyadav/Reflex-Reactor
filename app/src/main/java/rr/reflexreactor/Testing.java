package rr.reflexreactor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Testing extends Activity {
    static long ActualFilelength = 0;
   // DeviceDetailFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_testing);
      //  fragment = (DeviceDetailFragment)getFragmentManager().findFragmentById(R.id.detail_frag);
    }

    public void send_data(View view) throws IOException {
        sendFile();
    }

    public void sendFile() throws IOException {
        //final File f = new File(this.getFilesDir(), "abc.txt");
        final File f = new File("/sdcard/" + "private" + ".txt");
        File dirs = new File(f.getParent());
        if (!dirs.exists())
            dirs.mkdirs();
        if(!f.exists())
            f.createNewFile();
        //writeToFile("hello world"+Math.random()%100);
        //Toast.makeText(this, readFromFile("abc.txt", this), Toast.LENGTH_SHORT).show();
        String Extension =f.getName();
        //Toast.makeText(getActivity(),Extension,Toast.LENGTH_LONG).show();
        Long FileLength=f.length();
        ActualFilelength=FileLength;
        Uri uri=Uri.fromFile(f);
        //Toast.makeText(getActivity(),uri.toString(),Toast.LENGTH_LONG).show();
        Intent serviceIntent = new Intent(this, FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        /**************************************************************************************/
        /*
    	 * Choose on which device file has to send whether its server or client
    	 */
        String OwnerIp = SharedPreferencesHandler.getStringValues(this, "GroupOwnerAddress");
        if (OwnerIp != null && OwnerIp.length() > 0) {
            String host=null;
            int  sub_port =-1;

            String ServerBool = SharedPreferencesHandler.getStringValues(this, "ServerBoolean");
            if (ServerBool!=null && !ServerBool.equals("") && ServerBool.equalsIgnoreCase("true")) {

                int count = Integer.parseInt(SharedPreferencesHandler.getStringValues(this, "count"));
                char ch='A';
                for ( int i=0;i<count;i++ ) {
                    String Ip = SharedPreferencesHandler.getStringValues(this,ch+"");
                    //Toast.makeText(getActivity(),"wifiClientIP:"+Ip,Toast.LENGTH_SHORT).show();
                    if (Ip != null && !Ip.equals("")) {
                        // Get Client Ip Address and send data
                        host = Ip;
                        sub_port = FileTransferService.PORT;
                        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, Ip);
                    }
                    /*******************************************************************/
                    serviceIntent.putExtra(FileTransferService.Extension, Extension);
                    serviceIntent.putExtra(FileTransferService.Filelength, ActualFilelength + "");
                    serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, FileTransferService.PORT);
                    if (host != null && sub_port != -1) {
//                        showprogress("Sending.....");
                        startService(serviceIntent);
                    } else {
//                        DismissProgressDialog();
                        Toast.makeText(this, "Host Address not found, Please Re-Connect", Toast.LENGTH_SHORT).show();
                    }
                    ch++;
                }
            }
            else {
                FileTransferService.PORT = 8888;
                host=OwnerIp;
                sub_port=FileTransferService.PORT;
                /*******************************************************/
                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, OwnerIp);
                serviceIntent.putExtra(FileTransferService.Extension, Extension);
                serviceIntent.putExtra(FileTransferService.Filelength, ActualFilelength + "");
                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, FileTransferService.PORT);
                if(host !=null && sub_port!=-1) {
                    //showprogress("Sending.....");
                    startService(serviceIntent);
                }
                else {
                    Toast.makeText(this,"Host Address not found, Please Re-Connect", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
           // DismissProgressDialog();
            Toast.makeText(this,"Host Address not found, Please Re-Connect", Toast.LENGTH_SHORT).show();
        }
    }

    public void writeToFile(String data) {
        FileOutputStream outputstream;
        try {
            outputstream = this.openFileOutput("abc.txt", Context.MODE_PRIVATE);
            outputstream.write(data.getBytes());
            outputstream.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(String filename,Context c) {

        String ret = "";

        try {
            InputStream inputStream = c.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Device detail frag", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("device detail frag", "Can not read file: " + e.toString());
        }

        return ret;
    }


}
