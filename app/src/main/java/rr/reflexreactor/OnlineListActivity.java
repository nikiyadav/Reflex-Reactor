package rr.reflexreactor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineListActivity extends Activity {
    ProgressDialog pDialog;
    JSONParser jsonParser=new JSONParser();
    String url_getAll_online="http://192.168.43.51/reflexreactor/getAll_online.php";
    JSONArray users;
    String TAG_USERS="users",TAG_PHONE="phn",TAG_RATING="rating";
    String[] user_names;
    ArrayList<String> unames;
    String[] user_phones;
    ListView listview;
    String phn;
    Context context=this;
    boolean[] clickedViews;
    int flag;
    ArrayList<String> user_id=new ArrayList<String>();
    String stored_reg_id;
    GCMClientManager pushClientManager;
    String reg_id;
    ConnectionDetector connectionDetector;
    Boolean connection=true;
    Handler handler;
    ArrayAdapter<String> adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_online_list);

        connectionDetector=new ConnectionDetector(this);
        context=this;
        unames=new ArrayList<>();
        listview=(ListView)findViewById(R.id.list_online);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String user_id=user_phones[(int)id];
                if(clickedViews[(int)id])
                    view.setBackgroundColor(Color.BLACK);
                else
                    view.setBackgroundColor(Color.parseColor("#d32f2f"));
                clickedViews[(int)id]=!clickedViews[(int)id];
            }
        });
        //Get Phone number from sharedPref
        //  phn=getPhnNumber();
        if(!connectionDetector.isConnectingToInternet())
        {
            LaunchInternetActivity();
        }
        else {
            pushClientManager = new GCMClientManager(this, GlobalUtils.PROJECT_NUMBER);
            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {

                    Log.d("Registration id:", registrationId);
                    reg_id = new String(registrationId);
                    if(isNewRegistration) {
                        SharedPreferences sharedPref = context.getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.saved_reg_id), reg_id);
                        editor.commit();
                    }
                    if(GlobalUtils.getPhnNumber(context).equals(""))
                    {
                        LaunchRegisterActivity();
                    }
                    else
                    {
                        new ShowOnline().execute();
                    }


                }

                @Override
                public void onFailure(String ex) {
                    Log.d("failure", "registration failed");
                    super.onFailure(ex);
                    //     failure=1;
                    //  Toast.makeText(context,"You are offline",Toast.LENGTH_SHORT);
                    //if(!connectionDetector.isConnectingToInternet())
                    //{
                    Log.d("connectionHandler","No connection");
                    LaunchInternetActivity();

//                    }

                }
            });
            // new GetOnlineUsers().execute();
            handler = new Handler();

            //new Check_Valid_Phn().execute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        flag=1;
        new ShowOnline().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        flag=0;
        new ShowOnline().execute();
    }

    public void challenge_action(View v)
    {
        int count=0;
        Log.d("ChallengePressed","1");
        for(int i=0;i<user_phones.length;i++)
        {
            if(clickedViews[i]==true)
            {
                count++;
                user_id.add(user_phones[i]);

            }
        }
        if(count>0) {
            if (count > 6)
                Message.message(this, "Maximum number of players can be 6");
            else {
                Intent intent = new Intent(context, Level_catagory.class);
                intent.putExtra("mode", "online");
                intent.putExtra("user_id", user_id);
          /*  user_names=new String[unames.size()];
            for(int i=0;i<unames.size();i++)
            {
                user_names[i]=new String(unames.get(i));
            }*/
                String[] user_names1;
                user_names1 = new String[count];
                int j = 0;
                for (int i = 0; i < user_phones.length; i++) {
                    if (clickedViews[i] == true) {

                        user_names1[j] = user_names[i];
                        j++;
                    }
                }
                intent.putExtra("user_names", user_names1);
                startActivity(intent);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No one is selected",Toast.LENGTH_SHORT).show();
        }
    }
    class GetOnlineUsers extends AsyncTask<String, String, String> {

        String msg;
        int successResult;
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            phn=GlobalUtils.getPhnNumber(context);
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
                params.add(new BasicNameValuePair("phone", phn));
                // getting JSON string from URL

                JSONObject json = jsonParser.makeHttpRequest(url_getAll_online, "POST", params);

                // Check your log cat for JSON reponse
                Log.d("All Articles: ", json.toString());
                System.out.println(json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt("success");

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        users = json.getJSONArray(TAG_USERS);
                        // looping through All Products

                        System.out.println("working fine until now");

                        unames.clear();
                        user_phones = new String[users.length()];
                        user_names=new String[users.length()];
                        clickedViews = new boolean[users.length()];
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject c = users.getJSONObject(i);
                            // Storing each json item in variable
                            String phone = c.getString(TAG_PHONE);
                            int rating = c.getInt(TAG_RATING);
                            String uname = c.getString("uname");
                            unames.add(uname);
                            user_phones[i] = phone;
                            user_names[i]=uname;
                            clickedViews[i] = false;
                        }
                        successResult = 1;

                    } else if (success == 2) {
                        successResult = 1;
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
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                } else {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            /**
                             * Updating parsed JSON data into ListView
                             * */
                            //if(listview.getAdapter()==null) {
                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, unames);
                            listview.setAdapter(adapter);
                            //}   else{
                            //  ((ArrayAdapter)listview.getAdapter()).notifyDataSetChanged();

                            // }
                        }
                    });
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new GetOnlineUsers().execute();
                    }
                }, 5000);
            }
        }

    }


    private List<String> autocomplete(String input) {
        // don't use the here the resultList List on which the adapter is based!
        // some custom code to get items from http connection
        ArrayList<String> queryResults = new ArrayList<String>(); // new list
        queryResults.add("Some String");
        return queryResults;
    }
    class ShowOnline extends AsyncTask<String, String, String> {
        String msg;
        int successResult=0;
        boolean on=false;
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            phn=GlobalUtils.getPhnNumber(context);
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("ShowOnline","No connection");
                if(flag==1) {
                    LaunchInternetActivity();
                }

            }
            if(flag==1)
                on=true;

        }

        protected String doInBackground(String... args) {

            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phone", phn));

                //If called from onstart on is true else false
                if (on)
                    params.add(new BasicNameValuePair("on", "1"));

                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_online,
                        "POST", params);

                try {
                    int success = json.getInt("success");
                    //   System.out.println("In multi_player.class " + json.toString());
                    Log.d("In multi_player.class ", json.toString() + ":end");
                    if (success == 1) {
                        successResult = 1;
                        msg = "Request sent successfully";
                    } else {
                        successResult = 0;
                        msg = json.getString("message") + ";It's message";
                    }
                } catch (Exception e) {

                }


            }
            return null;
        }

        protected void onPostExecute(String file_url) {
//            Log.d("sucessresult", msg+":endMsg");
            if(connection&&flag==1)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new GetOnlineUsers().execute();
                    }
                });

            }
        }
    }
    class Check_Valid_Phn extends AsyncTask<String, String, String> {
        String msg;
        int successResult=0;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            phn=GlobalUtils.getPhnNumber(context);
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("Check_valid_phn","No connection");
                LaunchInternetActivity();

            }

        }

        protected String doInBackground(String... args) {

            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_phn", phn));

                //If called from onstart on is true else false

                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_check_valid_phn,
                        "POST", params);

                try {
                    int success = json.getInt("success");

                    if (success == 1) {
                        successResult = 1;
                        msg = "valid";
                        stored_reg_id = json.getString("reg_id");

                    } else {
                        successResult = 0;
                    }
                } catch (Exception e) {

                }
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
//            Log.d("sucessresult", msg+":endMsg");
            if(connection) {
                if (successResult == 1) {
                    Log.d("Check_valid_phn", "1");
                    check_valid_reg_id();
                    //new ShowOnline().execute();
                } else {
                    save_reg_id();
                }
            }
        }
    }
    void LaunchRegisterActivity()
    {
        if(!connectionDetector.isConnectingToInternet())
        {
            Log.d("LaunchRegister","No connection");
            LaunchInternetActivity();

        }
        else {
            Intent intent = new Intent(this, Register.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
    void save_reg_id()
    {
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {

                Log.d("Registration id:", registrationId);
                reg_id = new String(registrationId);
                if(isNewRegistration) {
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.saved_reg_id), reg_id);
                    editor.commit();
                    LaunchRegisterActivity();
                }
                else
                {
                    Intent intent=new Intent(OnlineListActivity.this,InternetActivity.class);
                    intent.putExtra("source","OnlineListActivity");
                    startActivity(intent);
                    finish();
                }
                // LaunchRegisterActivity();

            }

            @Override
            public void onFailure(String ex) {
                Log.d("failure", "registration failed");
                super.onFailure(ex);
                //     failure=1;
                Toast.makeText(context,"You are offline",Toast.LENGTH_SHORT);
                if(!connectionDetector.isConnectingToInternet())
                {
                    Log.d("connectionHandler","No connection");
                    LaunchInternetActivity();

                }

            }
        });
    }
    void check_valid_reg_id()
    {
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {

                Log.d("Registration id:", registrationId);
                reg_id = new String(registrationId);
                if (isNewRegistration) {
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.saved_reg_id), reg_id);
                    editor.commit();
                }
                if (!stored_reg_id.equals(reg_id))
                    new Update_Reg_id().execute();
                else
                    new ShowOnline().execute();

            }

            @Override
            public void onFailure(String ex) {
                Log.d("failure", "registration failed");
                super.onFailure(ex);
                //     failure=1;
                if(!connectionDetector.isConnectingToInternet())
                {
                    Log.d("connectionHandler","No connection");
                    LaunchInternetActivity();

                }

            }
        });

    }
    class Update_Reg_id extends AsyncTask<String, String, String> {
        String msg;
        int successResult=0;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("Update_reg_ID","No connection");
                LaunchInternetActivity();

            }
            //reg_id=GlobalUtils.getRegistrationId(context);
        }

        protected String doInBackground(String... args) {

            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_phn", phn));
                params.add(new BasicNameValuePair("reg_id", reg_id));


                //If called from onstart on is true else false


                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_update_reg_id,
                        "POST", params);

                try {
                    int success = json.getInt("success");

                    if (success == 1) {
                        successResult = 1;
                        msg = "updated";


                    } else {
                        successResult = 0;
                        msg = json.getString("message");
                    }
                } catch (Exception e) {

                }
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
//            Log.d("sucessresult", msg+":endMsg");
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT);
            if(connection) {
                //LaunchMainActivity();
                new ShowOnline().execute();
            }

        }
    }
    public void LaunchInternetActivity()
    {
        connection=false;
        Intent intent=new Intent(OnlineListActivity.this,InternetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("source", "OnlineListActivity");
        startActivity(intent);
        finish();
    }

}
