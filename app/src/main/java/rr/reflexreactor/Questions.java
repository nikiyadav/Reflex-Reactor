package rr.reflexreactor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Questions extends Activity {

    ArrayList<String> My_question;
    MyDatabaseAdapter dbasehelper;
    MyDatabaseAdapter.MyHelper helper;
    CountDownTimer cTimer;
    String game_id,mode;

    String correct;
    Button optnA,optnB,optnC,optnD,reset,submit;
    TextView tv,selected_optn,show_correct;
    TextView que_no,ques,timer;
    int my_level;
    int number_of_catagories;
    String[] catagories_name = new String[15];
    int[] catagorie_id = new int[15];
    int[] question_id = new int[15];
    String[] total_data = new String[100];
    int p=0;
    int time_value=20000;
    int final_score=0;
    int Time_passed;
    Context context;

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("Are you sure you want Exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cTimer.cancel();
                        Intent intent = new Intent(Questions.this, Mode.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Exit me", true);
                        startActivity(intent);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_questions);
        tv = (TextView)findViewById(R.id.My_question);
        selected_optn = (TextView)findViewById(R.id.selected_option);
        Intent intent_for_game_id = getIntent();
        intent_for_game_id.getExtras();
        game_id = intent_for_game_id.getStringExtra("game_id");
        mode = intent_for_game_id.getStringExtra("mode");
        reset = (Button)findViewById(R.id.reset);
        submit = (Button)findViewById(R.id.submit);
        show_correct = (TextView)findViewById(R.id.show_correct);
        context= getApplicationContext();

        BufferedReader br = null;
        String response = null;


        try {

            StringBuffer output = new StringBuffer();
            String fpath = "/sdcard/"+"private"+".txt";

            br = new BufferedReader(new FileReader(fpath));
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line+"");
            }
            response = output.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        total_data = response.split(" ");

        my_level = Integer.parseInt(total_data[0]);
        number_of_catagories=Integer.parseInt(total_data[1]);
        for(int i=0;i<number_of_catagories;i++)
        {
            catagories_name[i]=total_data[i+2];
        }
        for(int i=0;i<10;i++)
        {
            catagorie_id[i]=Integer.parseInt(total_data[number_of_catagories+2+i]);
        }
        for(int i=0;i<10;i++)
        {
           question_id[i]=Integer.parseInt(total_data[number_of_catagories+12+i]);
        }


        que_no = (TextView) findViewById(R.id.Question_No_text);
        ques = (TextView) findViewById(R.id.My_question);
        optnA = (Button) findViewById(R.id.option1);
        optnB = (Button) findViewById(R.id.option2);
        optnC = (Button) findViewById(R.id.option3);
        optnD = (Button) findViewById(R.id.option4);
        timer = (TextView) findViewById(R.id.timer);

        if(my_level==1)
        {
            time_value=30000;
        }
        else if(my_level==2)
        {
            time_value=20000;
        }
        else if(my_level==3)
        {
            time_value=15000;
        }
        else
        {
            time_value=10000;
        }
        cTimer = new CountDownTimer(time_value, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Time_passed++;
                timer.setText("Time : " + millisUntilFinished / 1000);
            }
            @Override
            public void onFinish() {
                dont_store_next_question_without_view();
            }
        }.start();

        dbasehelper = new MyDatabaseAdapter(this);
        helper = new MyDatabaseAdapter.MyHelper(this);
        try {
            helper.createDataBase();
        } catch (IOException e) {
            throw new Error("Unable to create database");
        }
        try {
            helper.openDataBase();
        } catch (SQLException e) {
        }

        My_question = dbasehelper.getSubjectData(catagories_name[catagorie_id[p]],question_id[p]);

        que_no.setText("NO : " + (p+1) + "");
        ques.setText("Question ??? \n" + My_question.get(1));

        int[] mpoptns = new int[4];
        for(int i=0;i<4;i++)
        {            mpoptns[i]=-1;
        }
        Random random = new Random();
        for(int i=1;i<=4;i++)
        {
            int idx = random.nextInt(4);
            if(mpoptns[idx]==-1)
            {
                mpoptns[idx]=i;
            }
            else
                i--;
        }

         correct = get_correct(mpoptns);

        optnA.setText("A) " + My_question.get(mpoptns[0]+1));
        optnB.setText("B) " + My_question.get(mpoptns[1]+1));
        optnC.setText("C) " + My_question.get(mpoptns[2]+1));
        optnD.setText("D) " + My_question.get(mpoptns[3]+1));

        p++;

    }

    private void dont_store_next_question_without_view() {

        //  score[score_count]=0;
        //  score_count++;
        if (p == 10) {
            p++;
            cTimer.cancel();

            String online_mode = "online_from_send_request";
            if(mode.equals(online_mode))
            {
                Intent intent = new Intent(this, scoreboard2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("game_id",game_id);
                intent.putExtra("mode",mode);
                intent.putExtra("final_score",final_score);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(this, Score_board_solo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("final_score",final_score);
                startActivity(intent);
            }

        } else if (p < 10) {

            selected_optn.setText("");
            optnA.setTextColor(Color.WHITE);
            optnA.setEnabled(true);
            optnB.setTextColor(Color.WHITE);
            optnB.setEnabled(true);
            optnC.setTextColor(Color.WHITE);
            optnC.setEnabled(true);
            optnD.setTextColor(Color.WHITE);
            optnD.setEnabled(true);
            reset.setEnabled(true);
            submit.setEnabled(true);
            show_correct.setText("");
            Time_passed=0;

            My_question = dbasehelper.getSubjectData(catagories_name[catagorie_id[p]], question_id[p]);

            que_no.setText("NO : " + (p+1) + "");
            ques.setText("Question ??? \n" + My_question.get(1));

            int[] mpoptns = new int[4];
            for (int i = 0; i < 4; i++) {
                mpoptns[i] = -1;
            }
            Random random = new Random();
            for (int i = 1; i <= 4; i++) {
                int idx = random.nextInt(4);
                if (mpoptns[idx] == -1) {
                    mpoptns[idx] = i;
                } else
                    i--;
            }

            correct = get_correct(mpoptns);

            optnA.setText("A) " + My_question.get(mpoptns[0] + 1));
            optnB.setText("B) " + My_question.get(mpoptns[1] + 1));
            optnC.setText("C) " + My_question.get(mpoptns[2] + 1));
            optnD.setText("D) " + My_question.get(mpoptns[3] + 1));

            p++;
        }
        cTimer.cancel();
        cTimer.start();
    }

    private String get_correct(int[] mpoptns) {
        char[] ans = new char[4];
        ans[mpoptns[0]-1]='A';
        ans[mpoptns[1]-1]='B';
        ans[mpoptns[2]-1]='C';
        ans[mpoptns[3]-1]='D';
        String ans_str =  new String(ans);
        return ans_str;
    }

    public void select_option(View view) {
        Button option = (Button)view;
        String str = option.getText().toString();
        char ch =str.charAt(0);
        selected_optn.append(ch + "");
        option.setTextColor(Color.BLACK);
        option.setEnabled(false);
    }

    public void reset_option(View view) {
        selected_optn.setText("");
        optnA.setTextColor(Color.WHITE);
        optnA.setEnabled(true);
        optnB.setTextColor(Color.WHITE);
        optnB.setEnabled(true);
        optnC.setTextColor(Color.WHITE);
        optnC.setEnabled(true);
        optnD.setTextColor(Color.WHITE);
        optnD.setEnabled(true);
    }

    public void submit_answer(View view) {
        String final_answer = selected_optn.getText().toString();
        if(final_answer.length()==4) {

            reset.setEnabled(false);
            Button sub = (Button)view;
            sub.setEnabled(false);

            char[] corr = correct.toCharArray();
            char[] fin = final_answer.toCharArray();
            int lcs_value = get_LCS_count(corr,fin,4,4);
            int dummy_value=0;
            if(lcs_value==1)
            {
                dummy_value = -10;
            }
            else if(lcs_value==2)
            {
                dummy_value=-5;
            }
            else if(lcs_value==3)
            {
                dummy_value=10;
            }
            else if(lcs_value==4)
            {
                dummy_value=20;
            }
            dummy_value = dummy_value + (dummy_value*((time_value/1000)-Time_passed))/(time_value/1000);
            final_score+=dummy_value;
            show_correct.setText("Correct Ans : "+correct+ " You Get " + dummy_value);
            String solo= "solo_play";
            if(mode.equals(solo))
            {
                dont_store_next_question_without_view();
            }
        }
        else
        {
            Toast.makeText(this,"select Properly",Toast.LENGTH_SHORT).show();
        }
    }

    private int get_LCS_count(char[] correct, char[] weget,int m,int n) {
        if(m==0 || n==0)
            return 0;
        if(correct[m-1] == weget[n-1])
            return 1+ get_LCS_count(correct,weget,m-1,n-1);
        else
            return max(get_LCS_count(correct,weget,m,n-1),get_LCS_count(correct,weget,m-1,n));
    }

    private int max(int a, int b) {
        if(a>b)
            return a;
        else
            return b;
    }
}
