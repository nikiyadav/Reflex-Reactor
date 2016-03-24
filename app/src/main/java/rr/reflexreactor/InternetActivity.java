package rr.reflexreactor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class InternetActivity extends Activity {

    String source;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_internet);
        Intent intent=getIntent();
        source=intent.getStringExtra("source");
    }
    public void tryAgain_action(View view)
    {
        ConnectionDetector connectionDetector=new ConnectionDetector(this);
        if(connectionDetector.isConnectingToInternet())
        {
            if(source.equals("OnlineListActivity")) {
                Intent intent = new Intent(InternetActivity.this, OnlineListActivity.class);
                startActivity(intent);
                finish();
            }
//            else if(source.equals("Invites"))
//            {
//                Intent intent = new Intent(InternetActivity.this, Invites.class);
//                startActivity(intent);
//                finish();
//            }
            else if(source.equals("Multi_player"))
            {
                Intent intent = new Intent(InternetActivity.this, Offline_Online.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

}
