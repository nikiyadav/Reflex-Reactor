package rr.reflexreactor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Register extends Activity {

    EditText uname_et;
    EditText name_et;
    EditText phn_et;
    Button reg_btn;
    String uname;
    String name;
    String phn;
    String reg_id;
    String url_register="http://192.168.43.51/reflexreactor/register_online.php";
    JSONParser jsonParser=new JSONParser();
    ProgressDialog pDialog;
    Context context;
    boolean connection=true;
    ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_register);
        context=this;
        Log.d("In Register:","true");
        connectionDetector=new ConnectionDetector(context);

        uname_et=(EditText)findViewById(R.id.unameR_et);
        phn_et=(EditText)findViewById(R.id.phnR_et);
        reg_btn=(Button)findViewById(R.id.regR_btn);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Register_online().execute();
            }
        });

    }
    String getRegistrationId()
    {
        SharedPreferences sharedPref =this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.saved_reg_id),"");
    }
    class Register_online extends AsyncTask<String, String, String> {

        String msg;
        int successResult=0;
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(Register.this);
            if(!connectionDetector.isConnectingToInternet())
            {
                Log.d("connectionHandler","No connection");
                // Toast.makeText(context,"No Internet Connection!!",Toast.LENGTH_LONG);
                connection=false;
                Intent intent=new Intent(Register.this,InternetActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
            pDialog.setMessage("Registering..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            super.onPreExecute();

            //Get the values that user entered
            uname=uname_et.getText().toString();
            phn=phn_et.getText().toString();
            reg_id=getRegistrationId();

            //Store the phone-number that user entered in sharedPref
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.saved_phn), phn);
            editor.commit();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            if(connection) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phn", phn));
                params.add(new BasicNameValuePair("name", "name"));
                params.add(new BasicNameValuePair("uname", uname));
                params.add(new BasicNameValuePair("reg_id", reg_id));

                int[] tmp = new int[5];


                JSONObject json = jsonParser.makeHttpRequest(url_register,
                        "POST", params);

                try {
                    int success = json.getInt("success");
                    Log.d("ServerOutput:", "" + json.toString());

                    if (success == 1) {
                        successResult = 1;
                        msg = "Registered successfully";
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

            pDialog.dismiss();
            if (successResult == 1) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                SharedPreferences sharedPref = context.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_uname),uname);
                editor.putString(getString(R.string.matches_played),""+0);
                editor.putString(getString(R.string.matches_won),""+0);
                editor.putString(getString(R.string.gems),""+100);
                editor.putString(getString(R.string.rating),""+1000);
                editor.commit();
            }
            LaunchNextActivity();
        }
    }
    void LaunchNextActivity()
    {
        Intent intent = new Intent(this, OnlineListActivity.class);
        startActivity(intent);
        finish();
    }
    String getPhnNumber()
    {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.saved_phn),"");
    }
}
