package rr.reflexreactor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Answers extends Activity {

    String[] catagories_name_ans = new String[15];
    int[] catagorie_id_ans = new int[15];
    int[] question_id_ans = new int[15];
    int number_of_catagories_ans;
    int my_level_ans;
    String[] total_data_ans = new String[100];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_answers);

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

        total_data_ans = response.split(" ");

        my_level_ans = Integer.parseInt(total_data_ans[0]);
        number_of_catagories_ans=Integer.parseInt(total_data_ans[1]);
        for(int i=0;i<number_of_catagories_ans;i++)
        {
            catagories_name_ans[i]=total_data_ans[i+2];
        }
        for(int i=0;i<10;i++)
        {
            catagorie_id_ans[i]=Integer.parseInt(total_data_ans[number_of_catagories_ans+2+i]);
        }
        for(int i=0;i<10;i++)
        {
            question_id_ans[i]=Integer.parseInt(total_data_ans[number_of_catagories_ans+12+i]);
        }
    }


    public void get_me_answer(View view) {
        Button  btn  = (Button)view;
        String number_question = btn.getText().toString();
        String[] actual_ids = {"Question 1","Question 2","Question 3","Question 4","Question 5","Question 6","Question 7","Question 8","Question 9","Question 10"};
        if(number_question.equals(actual_ids[0]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[0]]);
            intent.putExtra("question_id_ans",question_id_ans[0]);
            startActivity(intent);
        }
        else if(number_question.equals(actual_ids[1]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[1]]);
            intent.putExtra("question_id_ans",question_id_ans[1]);
            startActivity(intent);
        }
        else if(number_question.equals(actual_ids[2]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[2]]);
            intent.putExtra("question_id_ans",question_id_ans[2]);
            startActivity(intent);
        }
        else if(number_question.equals(actual_ids[3]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[3]]);
            intent.putExtra("question_id_ans",question_id_ans[3]);
            startActivity(intent);
        }
        else if(number_question.equals(actual_ids[4]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[4]]);
            intent.putExtra("question_id_ans",question_id_ans[4]);
            startActivity(intent);
        }
        else if(number_question.equals(actual_ids[5]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[5]]);
            intent.putExtra("question_id_ans",question_id_ans[5]);
            startActivity(intent);
        }
        else if(number_question.equals(actual_ids[6]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[6]]);
            intent.putExtra("question_id_ans",question_id_ans[6]);
            startActivity(intent);
        }
        else if(number_question.equals(actual_ids[7]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[7]]);
            intent.putExtra("question_id_ans",question_id_ans[7]);
            startActivity(intent);
        }
        else if(number_question.equals(actual_ids[8]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[8]]);
            intent.putExtra("question_id_ans",question_id_ans[8]);
            startActivity(intent);
        }
        else if(number_question.equals(actual_ids[9]))
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[9]]);
            intent.putExtra("question_id_ans",question_id_ans[9]);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this,Dekh_lo_answer.class);
            intent.putExtra("table_name_ans",catagories_name_ans[catagorie_id_ans[10]]);
            intent.putExtra("question_id_ans",question_id_ans[10]);
            startActivity(intent);
        }


    }
}
