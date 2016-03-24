package rr.reflexreactor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MyDatabaseAdapter {

    MyHelper helper;
    Context c;

    public MyDatabaseAdapter(Context context) {
        helper = new MyHelper(context);
        c = context;
    }

    static class MyHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "questions.db";
        private static final String DB_PATH = "/data/data/rr.reflexreactor/databases/";
      //  public static final String TABLE_NAME = "science";
        public static final int DATABESE_VERSION = 3;
        public static final String UID = "_id";
        public static final String QUESTION = "question";
        public static final String OPTION_1 = "option1";
        public static final String OPTION_2 = "option2";
        public static final String OPTION_3 = "option3";
        public static final String OPTION_4 = "option4";


        private SQLiteDatabase myDataBase;
        private Context context;

        public MyHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABESE_VERSION);
            this.context = context;
        }

        public void createDataBase() throws IOException {
            boolean dbExist = checkDataBase();
            if (dbExist) {
                //do nothing - database already exist
            } else {
                //By calling this method and empty database will be created into the default system path
                //of your application so we are gonna be able to overwrite that database with our database.
                this.getReadableDatabase();
                try {
                    copyDataBase();
                } catch (IOException e) {
                    throw new Error("Error copying database");
                }
            }
        }

        private boolean checkDataBase() {
            SQLiteDatabase checkDB = null;
            try {
                String myPath = DB_PATH + DATABASE_NAME;
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            } catch (SQLiteException e) {
                //database does't exist yet.
            }
            if (checkDB != null) {
                checkDB.close();
            }
            return ((checkDB != null) ? true : false);
        }

        private void copyDataBase() throws IOException {
            //Open your local db as the input stream
            InputStream myInput = context.getAssets().open(DATABASE_NAME);
            // Path to the just created empty db
            String outFileName = DB_PATH + DATABASE_NAME;
            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

        public void openDataBase() throws java.sql.SQLException {
            //Open the database
            String myPath = DB_PATH + DATABASE_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }

        @Override
        public synchronized void close() {
            if (myDataBase != null)
                myDataBase.close();
            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //nothing in here
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //No need of updating
        }
    }

    public ArrayList<String> getSubjectData(String table_name,int my_random_number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {MyHelper.UID, MyHelper.QUESTION, MyHelper.OPTION_1, MyHelper.OPTION_2, MyHelper.OPTION_3, MyHelper.OPTION_4};
        Cursor cursor = db.query(table_name.toLowerCase(), columns, "_id="+my_random_number+"", null, null, null, null, null);
        ArrayList<String> list =  new ArrayList<String>();
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(MyHelper.UID);
            int index2 = cursor.getColumnIndex(MyHelper.QUESTION);
            int index3 = cursor.getColumnIndex(MyHelper.OPTION_1);
            int index4 = cursor.getColumnIndex(MyHelper.OPTION_2);
            int index5 = cursor.getColumnIndex(MyHelper.OPTION_3);
            int index6 = cursor.getColumnIndex(MyHelper.OPTION_4);
            String uidvalue = cursor.getString(index);
            String question_value = cursor.getString(index2);
            String option1 = cursor.getString(index3);
            String option2 = cursor.getString(index4);
            String option3 = cursor.getString(index5);
            String option4 = cursor.getString(index6);
            list.add(uidvalue);
            list.add(question_value);
            list.add(option1);
            list.add(option2);
            list.add(option3);
            list.add(option4);
        }
        return list;
    }
}
