package rr.reflexreactor;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import java.io.FileInputStream;

public class Stats extends Activity {

    Context context;

    String uname;
    String matches_played;
    String matches_won;
    String gems;
    String rating;
    TextView gem_cnt;
    TextView match_cnt;
    TextView rating_txt;
    TextView username_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stats);
        context=this;
        Bitmap bm=getImageBitmap(this,"profile","jpg");
        if(bm!=null) {
            CircleImageView imageView = (CircleImageView) findViewById(R.id.dp);
            imageView.setImageBitmap(bm);
        }

        findViewById(R.id.dp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 20);
            }
        });
        gem_cnt=(TextView)findViewById(R.id.gem_cnt);
        match_cnt=(TextView)findViewById(R.id.match_cnt);
        rating_txt=(TextView)findViewById(R.id.rating);
        username_txt=(TextView)findViewById(R.id.username);
        fetchStats();
    }

    public void fetchStats()
    {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        uname=sharedPref.getString(context.getString(R.string.saved_uname),"Username");
        matches_played=sharedPref.getString(context.getString(R.string.matches_played),"0");
        matches_won=sharedPref.getString(context.getString(R.string.matches_won),"0");
        rating=sharedPref.getString(context.getString(R.string.rating),"1000");
        gems= sharedPref.getString(context.getString(R.string.gems),"100");

        //
        gem_cnt.setText(gems);
        match_cnt.setText(matches_won+"/"+matches_played);
        rating_txt.setText(rating);
        username_txt.setText(uname);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String[] projection = new String[]{MediaStore.MediaColumns.DATA};
            CursorLoader cursorLoader = new CursorLoader(this,selectedImageUri, projection, null, null, null);
            Cursor cursor =cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);
            Bitmap bm;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath, options);
            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                    && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(selectedImagePath, options);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(selectedImagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    bm = rotateImage(bm, 0);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bm = rotateImage(bm, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bm = rotateImage(bm, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bm = rotateImage(bm,270);
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    bm = rotateImage(bm, 0);
                    break;
                default:
                    bm = rotateImage(bm, 90);
            }
            CircleImageView imageView=(CircleImageView)findViewById(R.id.dp);
            imageView.setImageBitmap(bm);
            saveImage(this,bm,"profile","jpg");
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    public void saveImage(Context context, Bitmap b,String name,String extension){
        name=name+"."+extension;
        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getImageBitmap(Context context,String name,String extension){
        name=name+"."+extension;
        try{
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        }
        catch(Exception e){
        }
        return null;
    }

}
