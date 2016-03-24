package rr.reflexreactor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Level_catagory extends FragmentActivity {

    static long ActualFilelength = 0;

    int number_of_catagories=0;
    String catagory_list[] = new String[15];
    int my_level=0;
    String mode;
    ArrayList<String> User_id;
    String[] user_names;
    int[] Category_id;
    int[] question_indexes;

    private static final int num_pages2=2;
    private ViewPager pager2;
    private PagerAdapter pagerAdapter2;

    static TextView mDotsTextb[];
    public int mDotsCountb;
    public LinearLayout mDotsLayoutb;
    Intent prev_intent;
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_level_catagory);
        Category_id = new int[20];
        question_indexes = new int[20];
        prev_intent = getIntent();
        prev_intent.getExtras();
        mode = prev_intent.getStringExtra("mode");
        User_id = prev_intent.getStringArrayListExtra("user_id");
        user_names=prev_intent.getStringArrayExtra("user_names");

        //if(mode==null) mode="multi_play";

        pager2 = (ViewPager)findViewById(R.id.pager2);
        pagerAdapter2 = new ScreenSlidePagerAdapter2(getSupportFragmentManager());
        pager2.setAdapter(pagerAdapter2);

        mDotsLayoutb = (LinearLayout)findViewById(R.id.image_countb);
        //here we count the number of images we have to know how many dots we need
        mDotsCountb = 2;

        //here we create the dots
        //as you can see the dots are nothing but "."  of large size
        mDotsTextb = new TextView[mDotsCountb];

        //here we set the dots
        for (int i = 0; i < mDotsCountb; i++) {
            mDotsTextb[i] = new TextView(this);
            mDotsTextb[i].setText(".");
            mDotsTextb[i].setTextSize(45);
            mDotsTextb[i].setTypeface(null, Typeface.BOLD);
            mDotsTextb[i].setTextColor(android.graphics.Color.GRAY);
            mDotsLayoutb.addView(mDotsTextb[i]);
        }
        mDotsTextb[0].setTextColor(Color.parseColor("#d32f2f"));

        pager2.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int pos) {
                super.onPageSelected(pos);
                for (int i = 0; i < mDotsCountb; i++) {
                    Level_catagory.mDotsTextb[i]
                            .setTextColor(Color.GRAY);
                }

                Level_catagory.mDotsTextb[pos]
                        .setTextColor(Color.parseColor("#d32f2f"));
            }
        });
    }

    public void selected(View view) {
        Button my_catagory = (Button)view;
        //Toast.makeText(this,my_catagory.getText().toString(),Toast.LENGTH_SHORT).show();
        my_catagory.setTextColor(Color.BLACK);
        my_catagory.setEnabled(false);
        catagory_list[number_of_catagories] = my_catagory.getText().toString();
        number_of_catagories++;
    }

    public void level_select(View view) {
        Button level_button = (Button)view;
        my_level = Integer.parseInt(level_button.getText().toString());
        level_button.setTextColor(Color.BLACK);
        Button btn1_level = (Button)findViewById(R.id.level1);
        Button btn2_level = (Button)findViewById(R.id.level2);
        Button btn3_level = (Button)findViewById(R.id.level3);
        Button btn4_level = (Button)findViewById(R.id.level4);
        btn1_level.setEnabled(true);
        btn2_level.setEnabled(true);
        btn3_level.setEnabled(true);
        btn4_level.setEnabled(true);
        level_button.setEnabled(false);
    }
    // Random selection
    public void quick_play(View view) throws IOException {
        try {
            String fpath = "/sdcard/" + "private" + ".txt";
            File file = new File(fpath);

            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            //  bw.write(s);
            Random random = new Random();
            int foo = random.nextInt(4)+1;
            my_level=foo;
            bw.write(foo + " ");
            foo = random.nextInt(10)+1;
            number_of_catagories=foo;
            bw.append(foo + " ");
            String foo_list[] = {"ECONOMICS","GENERAL","MISCELLANEOUS","CRICKET","GEOGRAPHY","MYTHOLOGY","HISTORY","INDIAN_CULTURE","MOVIES","SPORTS","POLITICS","SCIENCE"};
            for (int i = 0; i < foo; i++) {
                int aa = random.nextInt(12);
                catagory_list[i]=foo_list[aa];
                bw.append(foo_list[aa] + " ");
            }
            HashMap hm =  new HashMap();
            for (int i = 0; i < 10; i++) {
                int value = random.nextInt(number_of_catagories);
                int value2 = random.nextInt(7) + 1;
                if(hm.containsKey(value+""+value2))
                {
                    i--;
                }
                else
                {
                    hm.put(value+""+value2,"abc");
                    Category_id[i]=value;
                    question_indexes[i]=value2;
                }
            }
            for (int i = 0; i < 10; i++) {
                bw.append(Category_id[i] + " ");
            }
            for (int i = 0; i < 10; i++) {
                bw.append(question_indexes[i] + " ");
            }
            bw.close();

            // Log.d("Suceess", "Sucess");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String x="solo_play";
        String xx = "offline";
        String y = "online";
        if(mode.equals(x))
        {
            Intent intent = new Intent(this, Questions.class);
            intent.putExtra("mode","solo_play");
            startActivity(intent);
        }
        else if(mode.equals(xx))
        {
            sendFile();
            Intent intent = new Intent(this, Questions.class);
            intent.putExtra("mode","offline");
            startActivity(intent);
        }
        else if(mode.equals(y))
        {
         Intent intent = new Intent(this,Send_Request_Activity.class);
            intent.putExtra("idx",number_of_catagories);
            intent.putExtra("user_id",User_id);
            intent.putExtra("user_names",user_names);
            intent.putExtra("level",my_level);
            intent.putExtra("catagories",catagory_list);
            intent.putExtra("catagories_id",Category_id);
            intent.putExtra("question_indexes",question_indexes);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, Questions.class);
            intent.putExtra("mode","solo_play");
            startActivity(intent);
        }
    }
    // Create a file randomly from choosen catagories with 10 questions
    public void start_game(View view) throws IOException {

        if(my_level==0 || number_of_catagories==0)
        {
            Toast.makeText(this,"Please Select a Level and more than two categories",Toast.LENGTH_SHORT).show();
        }
        else if(number_of_catagories<=1)
        {
            Toast.makeText(this,"Please Select at least two categories",Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                String fpath = "/sdcard/" + "private" + ".txt";
                File file = new File(fpath);

                // If file does not exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                //  bw.write(s);
                HashMap hm =  new HashMap();
                bw.write(my_level + " ");
                bw.append(number_of_catagories + " ");
                for (int i = 0; i < number_of_catagories; i++) {
                    bw.append(catagory_list[i] + " ");
                }
                for (int i = 0; i < 10; i++) {
                    int value = random.nextInt(number_of_catagories);
                    int value2 = random.nextInt(7) + 1;
                    if(hm.containsKey(value+""+value2))
                    {
                        i--;
                    }
                    else
                    {
                        hm.put(value+""+value2,"abc");
                        Category_id[i]=value;
                        question_indexes[i]=value2;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    bw.append(Category_id[i] + " ");
                }
                for (int i = 0; i < 10; i++) {
                    bw.append(question_indexes[i] + " ");
                }
                bw.close();

                // Log.d("Suceess", "Sucess");

            } catch (IOException e) {
                e.printStackTrace();
            }

            String x="solo_play";
            String xx = "offline";
            String y = "online";
            if(mode.equals(x))
            {
                Intent intent = new Intent(this, Questions.class);
                intent.putExtra("mode","solo_play");
                startActivity(intent);
            }
            else if(mode.equals(xx))
            {
                sendFile();
                Intent intent = new Intent(this, Questions.class);
                intent.putExtra("mode","offline");
                startActivity(intent);
            }
            else if(mode.equals(y))
            {
                Intent intent = new Intent(this,Send_Request_Activity.class);
                intent.putExtra("idx",number_of_catagories);
                intent.putExtra("user_id",User_id);
                intent.putExtra("user_names",user_names);
                intent.putExtra("level",my_level);
                intent.putExtra("catagories",catagory_list);
                intent.putExtra("catagories_id",Category_id);
                intent.putExtra("question_indexes",question_indexes);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(this, Questions.class);
                intent.putExtra("mode","solo_play");
                startActivity(intent);
            }
        }
    }

    private class ScreenSlidePagerAdapter2 extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter2(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlideFragment2.create(position);
        }

        @Override
        public int getCount() {
            return num_pages2;
        }
    }





    // send file code down



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
