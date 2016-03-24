package rr.reflexreactor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class scoreboard2 extends Activity {

    String phn;
    String game_id;
    int score;
    ConnectionDetector connectionDetector;
    Boolean connection=true;
    JSONParser jsonParser=new JSONParser();
    Context context=this;
    boolean allUpdated=false;
    boolean isActivityVisible=false;
    android.os.Handler handler;
    ProgressDialog pDialog;
    String[] unames=new String[0];
    int[] scores=new int[0];
    ArrayList<HashMap<String,String>> users_results=new ArrayList<>();
    ListView listView;
    int rating;
    int delta_rating;
    int sum_score=0;
    int new_rating=0;
    TextView rating_tv;
    TextView delta_rating_tv;
    boolean won=false;
    int gems;
    int matches_played;
    int matches_won;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_scoreboard2);
        listView=(ListView)findViewById(R.id.users_result_list);
        rating_tv=(TextView)findViewById(R.id.rating_tv);
        delta_rating_tv=(TextView)findViewById(R.id.delta_rating_tv);
        connectionDetector=new ConnectionDetector(this);
        Intent intent=getIntent();
        game_id=intent.getStringExtra("game_id");
        score = intent.getIntExtra("final_score",0);
        rating=0;
        delta_rating=0;
        sum_score=0;
        // new AcceptRequest().execute();
        handler = new android.os.Handler();
        users_results=new ArrayList<>();

        new UpdateResult().execute();
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
    }

    public void answers(View view) {
        Intent intent = new Intent(this, Answers.class);
        startActivity(intent);
    }

    class UpdateResult extends AsyncTask<String, String, String> {

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
            else
            {
                pDialog=new ProgressDialog(scoreboard2.this);
                pDialog.setMessage("Fetching Results..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
            phn= GlobalUtils.getPhnNumber(context);
        }

        @Override
        protected String doInBackground(String... args) {

            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phn", phn));
                params.add(new BasicNameValuePair("game_id",game_id));
                params.add(new BasicNameValuePair("score",""+score));


                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_update_result,
                        "POST", params);

                try {
                    int success = json.getInt("success");
                    //   System.out.println("In multi_player.class " + json.toString());
                    Log.d("In scoreboard2.class ", json.toString() + ":end");
                    if (success == 1) {
                        successResult = 1;
                        msg = "Result Updated successfully";
                        Log.d("UpdateSuccess1:","1");
                    } else {
                        successResult = 0;
                        msg = json.getString("message") + ";It's message";
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

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new checkAllUpdated().execute();
                        }
                    });


                }
            }
        }
    }


    class checkAllUpdated extends AsyncTask<String, String, String> {

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

                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_check_all_updated,
                        "POST", params);

                try {
                    int success = json.getInt("success");
                    //   System.out.println("In multi_player.class " + json.toString());
                    Log.d("In scoreboard2.class ",json.toString() + ":end");
                    if (success == 1) {
                        successResult = 1;
                        msg = "ALL Updated";
                        Log.d("AllUpdateSuccess1:","1");
                    } else {
                        successResult = 0;
                        msg = json.getString("message") + ";It's message";
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
                if(successResult!=1)
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new checkAllUpdated().execute();
                        }
                    });

                }
                else
                {
                    new FetchResult().execute();
                }
            }
        }
    }
    class FetchResult extends AsyncTask<String, String, String> {

        int successResult=0;
        String msg;
        JSONArray results;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("UpdateResult", "No connection");
                LaunchInternetActivity();
            }
            else {
                phn = GlobalUtils.getPhnNumber(context);
            }
        }

        @Override
        protected String doInBackground(String... args) {

            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("game_id",game_id));

                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_fetch_result,
                        "POST", params);

                try {
                    int success = json.getInt("success");
                    //   System.out.println("In multi_player.class " + json.toString());
                    Log.d("In scoreboard2.class ", json.toString() + ":end");
                    if (success == 1) {

                        results=json.getJSONArray("results");
                        // unames=new String[results.length()];
                        //scores=new int[results.length()];
                        for(int i=0;i<results.length();i++)
                        {
                            JSONObject c=results.getJSONObject(i);
                            String uname_i=c.getString("uname");
                            int score_i=c.getInt("score");
                            Log.d("Score_i",""+score_i);
                            int rating_i=c.getInt("rating");
                            int gems_i=c.getInt("gems");
                            int matches_played_i=c.getInt("matches_played");
                            int matches_won_i=c.getInt("matches_won");
                            String phn_i=c.getString("phn");
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("uname",uname_i);
                            hashMap.put("score",""+score_i);
                            hashMap.put("rating",""+rating_i);
                            hashMap.put("phn",phn_i);
                            hashMap.put("gems",""+gems_i);
                            hashMap.put("matches_played",""+matches_played_i);
                            hashMap.put("matches_won",""+matches_won_i);
                            users_results.add(hashMap);

                        }
                        successResult = 1;
                        msg="fetched successfully";
                        Log.d("Fetched successfully","1");
                    } else {
                        successResult = 0;
                        msg = json.getString("message") + ";It's message";
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
                    Log.d("ResultFetched","1");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ListAdapter adapter = new SimpleAdapter(
                                    scoreboard2.this, users_results,
                                    R.layout.single_row_result, new String[]{"uname",
                                    "rating","score"},
                                    new int[]{R.id.uname_srow_result, R.id.rating_srow_result,R.id.score_srow_result});
                            // updating listview
                            listView.setAdapter(adapter);
                        }
                    });
                    calculate_rating();
                }
            }
        }
    }
    void calculate_rating()
    {
        int old_rating=0;
        int avg_score;
        int max_score=0;
        String id_phn=GlobalUtils.getPhnNumber(this);

        for(int i=0;i<users_results.size();i++)
        {
            HashMap<String,String> hashMap=users_results.get(i);
            String phn_i=hashMap.get("phn");
            int score_i=Integer.parseInt(hashMap.get("score").toString());
            if(phn_i.equals(id_phn))
            {
                old_rating=Integer.parseInt(hashMap.get("rating"));
                matches_played=Integer.parseInt(hashMap.get("matches_played"));
                matches_won=Integer.parseInt(hashMap.get("matches_won"));
                gems=Integer.parseInt(hashMap.get("gems"));
            }
            if(max_score<score_i)
                max_score=score_i;
            sum_score=sum_score+score_i;
        }
        matches_played=matches_played+1;
        if(max_score==score)
        {
            won=true;
            gems=gems+10;
            matches_won=matches_won+1;
        }

        avg_score=sum_score/users_results.size();
        delta_rating=score-avg_score;
        new_rating=old_rating+delta_rating;
        rating_tv.setText(""+new_rating);
        if(delta_rating>0)
            delta_rating_tv.setText("+"+delta_rating);
        else
            delta_rating_tv.setText(""+delta_rating);
        for(int i=0;i<users_results.size();i++)
        {
            HashMap<String,String> hashMap=users_results.get(i);
            int score_i=Integer.parseInt(hashMap.get("score").toString());
            int old_rating_i=Integer.parseInt(hashMap.get("rating").toString());
            int new_rating=old_rating_i+score_i-avg_score;
            hashMap.remove("rating");
            hashMap.put("rating",""+new_rating);
        }
        updateStats();

        runOnUiThread(new Runnable() {
            public void run() {
                ListAdapter adapter = new SimpleAdapter(
                        scoreboard2.this, users_results,
                        R.layout.single_row_result, new String[]{"uname",
                        "rating","score"},
                        new int[]{R.id.uname_srow_result, R.id.rating_srow_result,R.id.score_srow_result});
                // updating listview
                listView.setAdapter(adapter);
            }
        });
        new UpdateRating().execute();
    }
    void updateStats()
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.matches_played),""+matches_played);
        editor.putString(getString(R.string.matches_won),""+matches_won);
        editor.putString(getString(R.string.gems),""+gems);
        editor.putString(getString(R.string.rating),""+new_rating);
        editor.commit();
    }
    class UpdateRating extends AsyncTask<String, String, String> {

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
                params.add(new BasicNameValuePair("phn", phn));
                params.add(new BasicNameValuePair("rating", "" + new_rating));
                if(won)
                    params.add(new BasicNameValuePair("won",""+1));


                JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_update_rating,
                        "POST", params);

                try {
                    int success = json.getInt("success");
                    //   System.out.println("In multi_player.class " + json.toString());
                    Log.d("In scoreboard2.class ", json.toString() + ":end");
                    if (success == 1) {
                        successResult = 1;
                        msg = "Rating Updated successfully";
                        Log.d("UpdateSuccess1:","1");
                    } else {
                        successResult = 0;
                        msg = json.getString("message") + ";It's message";
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
                pDialog.dismiss();
                if(successResult==1)
                {
                    Log.d("UpdateRating",""+new_rating);
                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Error in updateRating",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    class  AcceptRequest extends AsyncTask<String, String, String> {
        String msg;
        String phn;
        int successResult=0;
        /**
         * Before starting background thread Show Progress Dialog
         */
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

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("phn",phn));

            params.add(new BasicNameValuePair("game_id", game_id));
            params.add(new BasicNameValuePair("score", "" + score));
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(GlobalUtils.url_accept_request, "POST", params);

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
                    msg="Accept request sent";
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

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

        }

    }
    public void LaunchInternetActivity()
    {
        connection=false;
        Intent intent=new Intent(scoreboard2.this,InternetActivity.class);
        intent.putExtra("source", "scoreboard2");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(scoreboard2.this,Mode.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}