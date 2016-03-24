package rr.reflexreactor;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Mode extends Activity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mode);
    }

    public void soloplay(View view) {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
        mp.start();
        intent = new Intent(this,Level_catagory.class);
        intent.putExtra("mode","solo_play");
        startActivity(intent);
    }

    public void multiplay(View view) {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
        mp.start();
        intent = new Intent(this,Offline_Online.class);
        intent.putExtra("mode","multi_play");
        startActivity(intent);
    }

    public void help(View view) {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
        mp.start();
        intent = new Intent(this,Help.class);
        intent.putExtra("mode","help");
        startActivity(intent);
    }

    public void stats(View view) {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
        mp.start();
        intent = new Intent(this,Stats.class);
        intent.putExtra("mode","stats");
        startActivity(intent);
    }
}
