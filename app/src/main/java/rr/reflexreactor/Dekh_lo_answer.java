package rr.reflexreactor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Dekh_lo_answer extends Activity {


    MyDatabaseAdapter dbasehelper;
    MyDatabaseAdapter.MyHelper helper;
    String table_name;
    int question_id_ans;
    ArrayList<String> My_question_ans;
    TextView question_ans_tv,correct_ans_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dekh_lo_answer);
        question_ans_tv = (TextView)findViewById(R.id.My_question_ans);
        correct_ans_tv = (TextView)findViewById(R.id.correct_ans);
        Intent intent = getIntent();
        intent.getExtras();
        table_name = intent.getStringExtra("table_name_ans");
        question_id_ans = intent.getIntExtra("question_id_ans", 0);

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

        My_question_ans = dbasehelper.getSubjectData(table_name, question_id_ans);

        question_ans_tv.setText("Question ??? \n" + My_question_ans.get(1));
        correct_ans_tv.append("\n\n\nCorrect ans is\nA) "+My_question_ans.get(2)+"\nB) "+My_question_ans.get(3)+"\nC) "+My_question_ans.get(4)+"\nD) "+My_question_ans.get(5));

    }
}
