package rr.reflexreactor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Offline_Online extends Activity {

    private  boolean isWifiP2pEnabled=false;
    String mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_offline__online);
        Intent intent = getIntent();
        intent.getExtras();
        mode = intent.getStringExtra("mode");
    }

    public void offline_play(View view) {

        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()){
            //go to next activity
            Intent intent = new Intent(this,Discovery.class);
            intent.putExtra("mode", "multi_player");
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this,"enable your wifi",Toast.LENGTH_SHORT).show();
        }
    }

    public void online_play(View view) {
        Intent intent_my = new Intent(this,OnlineListActivity.class);
        intent_my.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent_my);
    }
}
