package rr.reflexreactor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Score_board_solo extends Activity {

    TextView score;
    public void onBackPressed() {
        Intent intent = new Intent(this, Mode.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_score_board_solo);
        Intent intent = getIntent();
        intent.getExtras();
        score = (TextView)findViewById(R.id.score_final);
        score.setText("Your Score " + intent.getIntExtra("final_score",0));
    }

    public void answers(View view) {
        Intent intent = new Intent(this, Answers.class);
        startActivity(intent);
    }
}
