package rr.reflexreactor;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends Activity {

    String game_id;
    String sender_name;
    ProgressDialog pDialog;
    JSONParser jsonParser=new JSONParser();
    Context context=this;
    String phn;
    int level;
    String category;
    String tables;
    String rows;
    TextView data_tv;
    public static boolean isActivityVisible=false;
    ConnectionDetector connectionDetector;
    boolean connection=true;
    Start_Game_Receiver receiver;
    Game_Terminated_Receiver game_terminated_receiver;
    boolean accepted=false;
    Button accept_btn;
    Button decline_btn;
    TextView sender_txt;
    TextView level_txt;
    TextView category_txt;
    boolean backwork=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_request);
        data_tv=(TextView)findViewById(R.id.data_tv);
        sender_txt=(TextView)findViewById(R.id.sender_txt);
        level_txt=(TextView)findViewById(R.id.level_txt);
        category_txt=(TextView)findViewById(R.id.category_txt);
        Intent intent=getIntent();
        game_id=intent.getStringExtra("game_id");
        sender_name=intent.getStringExtra("sender_name");
        backwork=true;
        accept_btn=(Button)findViewById(R.id.accept);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
        connectionDetector=new ConnectionDetector(this);
        decline_btn=(Button)findViewById(R.id.decline_btn);
        receiver=new Start_Game_Receiver();
        game_terminated_receiver=new Game_Terminated_Receiver();
        new ShowRequest().execute();

    }

    @Override
    public void onBackPressed() {
      //  new DeclineRequest().execute();
        if(backwork)
            super.onBackPressed();
    }

    private class Start_Game_Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String start_game_id=intent.getStringExtra("start_game_id");
            Log.d("ReceivedStartGameMSg","1");
            if(start_game_id.equals(game_id)&&accepted)
            {
                pDialog.dismiss();
                start_game();
            }
        }
    }
    private class Game_Terminated_Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String game_id=intent.getStringExtra("game_id");
            String sender_name=intent.getStringExtra("sender_name");
            Log.d("ReceivedGameTerminated","1");
            if(game_id.equals(game_id))
            {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(),sender_name+" left",Toast.LENGTH_LONG).show();
                accept_btn.setEnabled(false);
                decline_btn.setEnabled(false);
                backwork=true;
            }
        }
    }
    void start_game()
    {
//        String[] Table_name = category.split(",");
//        String[] Table_number1 = tables.split(",");
//        String[] Question_number1 = rows.split(",");
//        int[] Table_number = new int[6];
//        int[] Question_number = new int[6];
//        for(int i=0;i<6;i++)
//        {
//            Table_number[i] = Integer.parseInt(Table_number1[i]);
//            Question_number[i] = Integer.parseInt(Question_number1[i]);
//        }
        Intent intent = new Intent(this,Questions.class);
        intent.putExtra("game_id",game_id);
        intent.putExtra("mode","online_from_send_request");
//  intent.putExtra("level",level);
//        intent.putExtra("table_name",Table_name);
//        intent.putExtra("table_number", Table_number);
//        intent.putExtra("Question_number",Question_number);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityVisible=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("start_game"));
        LocalBroadcastManager.getInstance(this).registerReceiver(game_terminated_receiver,new IntentFilter("game_terminated"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(game_terminated_receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityVisible=false;
    }

    class ShowRequest extends AsyncTask<String, String, String> {
        String msg;
        int successResult=0;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("GetOnlineUSers","No connection");
                LaunchInternetActivity();

            }
            else {
                pDialog = new ProgressDialog(RequestActivity.this);
                pDialog.setMessage("Loading Request..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
                phn = GlobalUtils.getPhnNumber(context);
            }
            super.onPreExecute();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phone", phn));
                params.add(new BasicNameValuePair("game_id", game_id));
                // getting JSON string from URL
                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_show_request, "POST", params);

                // Check your log cat for JSON reponse
                Log.d("All Articles: ", json.toString());
                System.out.println(json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt("success");

                    if (success == 1) {
                        level = json.getInt("level");
                        category = json.getString("category");
                        tables = json.getString("tables");
                        rows = json.getString("rows");

                        //************

                        String[] category_split = category.split(",");
                        int length_cat = category_split.length;
                        String[] table_id_split = tables.split(",");
                        String[] question_id_split = rows.split(",");
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
                            bw.write(level + " ");
                            bw.append(length_cat + " ");
                            for (int i = 0; i < length_cat; i++) {
                                bw.append(category_split[i] + " ");
                            }
                            for (int i = 0; i < 10; i++) {
                                bw.append(table_id_split[i] + " ");
                            }
                            for (int i = 0; i < 10; i++) {
                                bw.append(question_id_split[i] + " ");
                            }
                            bw.close();

                            // Log.d("Suceess", "Sucess");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //************

                        successResult = 1;
                        msg = "Request Loaded Successfully";
                    } else if (success == 2) {
                        successResult = 2;
                        msg = json.getString("message");
                    } else {
                        successResult = 0;
                        msg = json.getString("message");
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            if(connection) {
                pDialog.dismiss();
                if(successResult==1) {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    showData();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Challenge Over!",Toast.LENGTH_LONG).show();
                    accept_btn.setEnabled(false);
                    decline_btn.setEnabled(false);
                    backwork=true;

                }
            }
        }

    }
    class AcceptRequest extends AsyncTask<String, String, String> {
        String msg;
        int successResult=0;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            phn=GlobalUtils.getPhnNumber(context);
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("AcceptRequest","No connection");
                LaunchInternetActivity();

            }
            else {
                backwork=false;
                pDialog = new ProgressDialog(RequestActivity.this);

                pDialog.setMessage("Waiting for response from host..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                super.onPreExecute();
            }
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phn", phn));
                params.add(new BasicNameValuePair("game_id", game_id));
                // getting JSON string from URL
                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_accept_request, "POST", params);

                // Check your log cat for JSON reponse
                Log.d("All Articles: ", json.toString());
                System.out.println(json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt("success");

                    if (success == 1) {
                        successResult=1;
                        msg = "Accepted Successfully";
                        accepted=true;
                    } else if (success == 2) {


                    } else {
                        successResult = 0;
                        msg = json.getString("message");
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            if(connection) {
               if(successResult==0)
               {
                   Toast.makeText(getApplicationContext(),"Error in connection!",Toast.LENGTH_SHORT).show();
               }
               else
               {

               }
            }
        }

    }
    void showData()
    {
        //data_tv.append("\n"+"\n"+"SENDER: "+sender_name + "\n" + "LEVEL: " + level+"\n");
        //data_tv.append("CATEGORIES: "+category+"\n");
        sender_txt.setText("SENDER: "+sender_name);
        level_txt.setText("LEVEL: "+level);
        category_txt.setText("CATEGORIES: "+category);
    }

    public void accept_me(View view) {

        /*pDialog = new ProgressDialog(RequestActivity.this);
        pDialog.setMessage("Loading game..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();*/
        backwork=false;
        new checkIfGameValid().execute();
      //  new AcceptRequest().execute();
    }
    class checkIfGameValid extends AsyncTask<String, String, String> {

        int successResult=0;
        String msg;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("UpdateResult", "No connection");
                LaunchInternetActivity();
            }
            phn= GlobalUtils.getPhnNumber(context);
        }

        @Override
        protected String doInBackground(String... args) {

            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("game_id",game_id));

                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_check_if_game_valid,
                        "POST", params);

                try {
                    int success = json.getInt("success");
                    //   System.out.println("In multi_player.class " + json.toString());
                    Log.d("In RequestActivity", json.toString() + ":end");
                    if (success == 1) {
                        successResult = 1;
                        msg = "Game VAlid";
                        Log.d("GameValidSuccess1:","1");
                    } else {
                        successResult = 0;
                        msg = json.getString("message") + ";It's an invalid game message";
                        Log.d("InvalidGame",msg);
                    }
                } catch (Exception e) {

                }


            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(connection)
            {
                if(successResult==1)
                {
                   new AcceptRequest().execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Game over!",Toast.LENGTH_LONG).show();
                    accept_btn.setVisibility(View.GONE);
                    backwork=true;
                }
            }
        }
    }
//    public void startChallenge()
//    {
//        String[] Table_name = category.split(",");
//        String[] Table_number1 = tables.split(",");
//        String[] Question_number1 = rows.split(",");
//        int[] Table_number = new int[6];
//        int[] Question_number = new int[6];
//        for(int i=0;i<6;i++)
//        {
//            Table_number[i] = Integer.parseInt(Table_number1[i]);
//            Question_number[i] = Integer.parseInt(Question_number1[i]);
//        }
//        Intent intent = new Intent(this,AcceptedChallenge.class);
//        intent.putExtra("game_id",game_id);
//        intent.putExtra("level",level);
//        intent.putExtra("table_name",Table_name);
//        intent.putExtra("table_number", Table_number);
//        intent.putExtra("Question_number",Question_number);
//        startActivity(intent);
//    }
    public void LaunchInternetActivity()
    {
        connection=false;
        Intent intent=new Intent(RequestActivity.this,InternetActivity.class);
        intent.putExtra("source", "RequestActivity");
        startActivity(intent);
        finish();
    }
    public void decline_action(View view)
    {
        new DeclineRequest().execute();
    }
    class DeclineRequest extends AsyncTask<String, String, String> {
        String msg;
        int successResult=0;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            phn=GlobalUtils.getPhnNumber(context);
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("DeclineRequest","No connection");
                LaunchInternetActivity();

            }
            else {
                pDialog = new ProgressDialog(RequestActivity.this);

                pDialog.setMessage("Processing..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                super.onPreExecute();
            }
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phn", phn));
                params.add(new BasicNameValuePair("game_id", game_id));
                // getting JSON string from URL
                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_decline_request, "POST", params);

                // Check your log cat for JSON reponse
                Log.d("DeclineResult: ", json.toString());
                System.out.println(json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt("success");
                    Log.d("DeclineSuccess",""+success);
                    if (success == 1) {
                        successResult=1;
                        msg = "Declined Successfully";
                        accepted=true;
                    } else if (success == 2) {


                    } else {
                        successResult = 0;
                        msg = json.getString("message");
                    }
                    Log.d("DeclineMsg",msg);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            if(connection) {
                pDialog.dismiss();
                /*if(successResult==0)
                {
                    Toast.makeText(getApplicationContext(),"Error in connection!",Toast.LENGTH_SHORT).show();
                }
                else
                {

                }*/
                Toast.makeText(getApplicationContext(),"You Declined",Toast.LENGTH_LONG).show();
                finishLoadMainActivity();
            }
        }

    }
    void finishLoadMainActivity()
    {
        Intent intent=new Intent(RequestActivity.this,Mode.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
