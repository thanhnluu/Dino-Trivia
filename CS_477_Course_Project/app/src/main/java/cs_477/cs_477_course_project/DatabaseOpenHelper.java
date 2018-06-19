package cs_477.cs_477_course_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Thanh_Luu on 12/10/2017.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    final static String TABLE_NAME = "users";   /*table name*/

    /*column names*/
    final static String USERNAME = "username";
    final static String PASSWORD = "password";
    final static String _ID = "_id";

    final private static String CREATE_CMD =
            "CREATE TABLE users (" + _ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " + USERNAME + " TEXT NOT NULL, " + PASSWORD +
                    " TEXT NOT NULL)";

    final private static String DATABASE_NAME = "todo_db";
    final private static Integer VERSION = 1;
    final private Context context;

    private SQLiteDatabase myDb = null;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creates table
        db.execSQL(CREATE_CMD);
    }

    public void insertUser(String username, String password) {
        ContentValues cv = new ContentValues();
        cv.put(USERNAME, username);
        cv.put(PASSWORD, password);
        if (myDb == null || !myDb.isOpen())
            myDb = getWritableDatabase();
        myDb.insert(TABLE_NAME, null, cv);
        myDb.close();
    }

    public void deleteUser(String id) {
        if (myDb == null || !myDb.isOpen())
            myDb = getWritableDatabase();
        myDb.delete(TABLE_NAME, "_id=?", new String[]{id});
        myDb.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    void deleteDatabase() {
        context.deleteDatabase(DATABASE_NAME);
    }
}