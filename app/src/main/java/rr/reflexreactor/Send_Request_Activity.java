package rr.reflexreactor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Send_Request_Activity extends Activity {

    TextView score1;
    ArrayList<String> user_id;
    String[] user_names;
    int level;
    String selected_list[];
    int[] question_indexes;
    MyDatabaseAdapter dbasehelper;
    int catagories_id[];
    int idx;
    String game_id;
    Context context;
    int score=0;
    ConnectionDetector connectionDetector;
    JSONParser jsonParser=new JSONParser();
    //public static boolean isActivityVisible=false;
    //  int isAccepted=0;
    //String[] user_names=new String[0];
    int count=0;
    boolean connection=true;
    String phn;
    ListView listView;
    JSONArray users;
    boolean started=false;
    int[] Table_number;
    int[] Question_number;
    String[] Table_name;
    boolean isActivityVisible=false;
    ProgressDialog pDialog;
    ArrayList<HashMap<String,String>> user_details;
    boolean oneAccepted=false;
    Button start_btn;
    boolean first=true;
    BaseAdapter adapter;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_send__request_);
        context=this;
        connectionDetector=new ConnectionDetector(context);
        listView = (ListView)findViewById(R.id.list_available);

        start_btn=(Button)findViewById(R.id.start_btn);
        start_btn.setEnabled(false);

        started=false;

        question_indexes = new int[6];
        Intent intent = getIntent();
        intent.getExtras();
        idx = intent.getIntExtra("idx", 0);
        user_id = intent.getStringArrayListExtra("user_id");
        user_names=intent.getStringArrayExtra("user_names");

        for(int i=0;i<user_names.length;i++)
        {
            Log.d("user_names",user_names[i]);
        }


        //   Log.d("user_names",user_names.toString());
        user_details=new ArrayList<HashMap<String, String>>();
        level = intent.getIntExtra("level", 0);
        catagories_id = intent.getIntArrayExtra("catagories_id");
        selected_list = intent.getStringArrayExtra("catagories");
        question_indexes = intent.getIntArrayExtra("question_indexes");
        handler=new Handler();

        //get score from intent and set score

        /*score1.append(user_id + "\n" + level + "\n");
        for(int i=0;i<6;i++)
        {
            score1.append(catagories_id[i]+" "+question_indexes[i]);
            score1.append("\n");
        }
        for(int i=0;i<idx;i++)
        {
            score1.append(selected_list[i].toString());
            score1.append("\n");
        }*/

        new Send_Game_Request().execute();
        //Display his score to the user

    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityVisible=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityVisible=false;
        //if(!started)
        //    new DeleteGame().execute();

    }
    class DeleteGame extends AsyncTask<String, String, String> {
        String msg;
        String phn;
        int successResult=0;
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            phn=GlobalUtils.getPhnNumber(context);
            super.onPreExecute();
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("GetOnlineUsers","No connection");
                LaunchInternetActivity();
            }
            else
            {
                pDialog=new ProgressDialog(Send_Request_Activity.this);
                pDialog.setMessage("Terminating Request..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                phn = GlobalUtils.getPhnNumber(context);
            }
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("phn",phn));
            params.add(new BasicNameValuePair("game_id",game_id));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_delete_game_request, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("All Articles: ", json.toString());
            //System.out.println(json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    successResult=1;
                    msg="Game terminated";
                    Log.d("GameTerminated","success");
                }
                else
                {
                    successResult=0;
                    msg=json.getString("message");
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {

            if(connection)
            {
                // if(successResult==1)
                //{
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Request Terminated",Toast.LENGTH_LONG).show();
                finishGameLoadMainActivity();
                //}
            }
        }

    }
    void finishGameLoadMainActivity()
    {
        Intent intent=new Intent(Send_Request_Activity.this,Mode.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onBackPressed() {
        if(!started)
            new DeleteGame().execute();
    }
    class Send_Game_Request extends AsyncTask<String, String, String> {
        String msg;
        String phn;
        int successResult=0;
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            phn=GlobalUtils.getPhnNumber(context);
            super.onPreExecute();
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("GetOnlineUsers","No connection");
                LaunchInternetActivity();
            }
            else
            {
                pDialog=new ProgressDialog(Send_Request_Activity.this);
                pDialog.setMessage("Processing..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                phn = GlobalUtils.getPhnNumber(context);
            }
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("sender_phn",phn));
            for(int i=0;i<user_id.size();i++)
            {
                String tmp="phn";
                tmp=tmp+(i+1);
                params.add(new BasicNameValuePair(tmp,user_id.get(i)));
            }
            params.add(new BasicNameValuePair("level",""+level));
            String category="";
            Table_name=new String[idx];
            for(int i=0;i<idx;i++)
            {
                Table_name[i]=selected_list[i];
                String tmp="cat";
                tmp=tmp+(i+1);
                if(i>0)
                    category=category+","+selected_list[i];
                else
                    category=category+selected_list[i];
                params.add(new BasicNameValuePair(tmp,selected_list[i]));
            }
            params.add(new BasicNameValuePair("numberOfCategories",""+idx));
            params.add(new BasicNameValuePair("category",category));
            params.add(new BasicNameValuePair("score",""+score));

            String tmp1,tmp2;
            tmp1="";
            tmp2="";
            Table_number=new int[10];
            Question_number=new int[10];
            for(int i=0;i<10;i++)
            {
                Table_number[i]=catagories_id[i];
                Question_number[i]=question_indexes[i];
                if(i==0)
                {
                    tmp1=tmp1+catagories_id[i];
                    tmp2=tmp2+question_indexes[i];
                }
                else
                {
                    tmp1 = tmp1 + "," + catagories_id[i];
                    tmp2 = tmp2 + "," + question_indexes[i];
                }
            }

            params.add(new BasicNameValuePair("tables",tmp1));
            params.add(new BasicNameValuePair("rows",tmp2));
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_send_game_request, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("All Articles: ", json.toString());
            //System.out.println(json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    successResult=1;
                    msg="Request sent successfully";
                    Log.d("messageSent",msg);
                    game_id=json.getString("game_id");
                    Log.d("Game_id",game_id);
                }
                else
                {
                    msg=json.getString("message");
                    Log.d("messageSent",msg);
                }

            } catch (JSONException e) {

                Log.d("game_id_catch","123:"+game_id);
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {

            if(connection)
            {
                pDialog.dismiss();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        new GetAcceptedUsers().execute();
                        //if(count<25)
                        //{
                        //  count++;

                        //}
                        //else
                        //{
                        //  Toast.makeText(getApplicationContext(),"None Accepted",Toast.LENGTH_LONG).show();
                        //}
                    }
                });

            }
        }

    }
    class GetAcceptedUsers extends AsyncTask<String, String, String> {

        String msg;
        int successResult;
        HashMap<String,String> done_users;
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            phn=GlobalUtils.getPhnNumber(context);
            done_users=new HashMap<String,String>();
            super.onPreExecute();
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("GetOnlineUSers","No connection");
                LaunchInternetActivity();
            }
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            if(connection) {    // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phn", phn));
                params.add(new BasicNameValuePair("game_id",game_id));
                // getting JSON string from URL

                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_getAcceptedUsers, "POST", params);

                // Check your log cat for JSON reponse
                Log.d("All Articles: ", json.toString());
                System.out.println(json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt("success");

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        users=json.getJSONArray("users");

                        user_details.clear();
                        //   user_names=new String[users.length()];
                        successResult = 1;

                        for (int i = 0; i < users.length(); i++) {

                            JSONObject c = users.getJSONObject(i);
                            String user_phn=c.getString("phn");
                            String user_name=c.getString("uname");
                            HashMap<String,String> user=new HashMap<>();
                            // Storing each json item in variable
                            // user_names[i] = c.getString("uname");
                            user.put("uname", user_name);
                            if(c.getInt("accepted")==1)
                            {
                                user.put("status","Accepted");
                                oneAccepted=true;
                            }
                            else
                                user.put("status","Invited");
                            done_users.put(user_phn,"1");
                            user_details.add(user);
                        }

                    } else if (success == 2) {

                        successResult = 1;
                        user_details.clear();

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

            //   pDialog.dismiss();
            if(connection) {
                if (successResult == 0) {
                    // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                } else {
                    if(oneAccepted)
                        start_btn.setEnabled(true);
                    for(int i=0;i<user_id.size();i++)
                    {
                        if(!done_users.containsKey(user_id.get(i)))
                        {
                            HashMap<String,String> user=new HashMap<>();
                            user.put("uname",user_names[i]);
                            Log.d("InPostExec",user_names[i]);
                            user.put("status","declined");
                            done_users.put(user_id.get(i),"1");
                            user_details.add(user);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            /**
                             * Updating parsed JSON data into ListView
                             * */
                            //       ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, user_names);
                            ///     listView.setAdapter(adapter);
                            //if(listView.getAdapter()==null) {
                                adapter = new SimpleAdapter(
                                        Send_Request_Activity.this, user_details,
                                        R.layout.single_row, new String[]{"uname",
                                        "status"},
                                        new int[]{R.id.uname_srow, R.id.status_srow});
                                // updating listview
                                listView.setAdapter(adapter);
                            //}
                            /*else
                            {

                                ((BaseAdapter)(listView.getAdapter())).notifyDataSetChanged();
                            }*/

                        }
                    });

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if(!started&&isActivityVisible)
                            new GetAcceptedUsers().execute();
                    }
                });
            }
        }

    }

    public void LaunchInternetActivity()
    {
        connection=false;
        Intent intent=new Intent(Send_Request_Activity.this,InternetActivity.class);
        intent.putExtra("source", "Multi_player");
        startActivity(intent);
        finish();
    }
    public void LaunchNextActivity()
    {
        //Toast.makeText(getApplicationContext(), "Accepted", Toast.LENGTH_LONG);
    }
    public void start_action(View view)
    {
        Log.d("Start_button_Pressed","1");
        started=true;
        new Start_Game().execute();
    }
    class Start_Game extends AsyncTask<String, String, String> {
        String msg;
        String phn;
        int successResult=0;
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            phn=GlobalUtils.getPhnNumber(context);
            super.onPreExecute();
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("StartGame","No connection");
                LaunchInternetActivity();
            }
            else
            {
                pDialog=new ProgressDialog(Send_Request_Activity.this);
                pDialog.setMessage("Starting Game..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                phn = GlobalUtils.getPhnNumber(context);
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
                params.add(new BasicNameValuePair("game_id",game_id));

                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_start_game, "POST", params);

                // Check your log cat for JSON response
                Log.d("All Articles: ", json.toString());
                //System.out.println(json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt("success");

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        successResult = 1;
                        msg = "Game started successfully";
                        Log.d("StartGame:","Success");


                    } else {
                        successResult=0;
                        msg = json.getString("message");
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            if(connection)
            {
                pDialog.dismiss();
                if(successResult==1) {
                    Log.d("HostGame:","started");
                    Toast.makeText(getApplicationContext(), "Game Started", Toast.LENGTH_SHORT).show();
                    start_game();
                }
            }
        }

    }
    void start_game()
    {
        Intent intent = new Intent(this,Questions.class);
        intent.putExtra("mode","online_from_send_request");
        intent.putExtra("game_id",game_id);
        intent.putExtra("level",level);
        intent.putExtra("table_name",Table_name);
        intent.putExtra("table_number", Table_number);
        intent.putExtra("Question_number",Question_number);
        startActivity(intent);
        finish();
    }

}
