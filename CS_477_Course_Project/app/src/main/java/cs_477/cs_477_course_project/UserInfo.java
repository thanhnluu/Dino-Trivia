package cs_477.cs_477_course_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static cs_477.cs_477_course_project.DatabaseOpenHelper.TABLE_NAME;

public class UserInfo extends AppCompatActivity {

    TextInputLayout tilFullname, tilPassword;
    TextView points, username;
    ProgressDialog progress;
    String apilink = "https://arcane-badlands-47658.herokuapp.com/api/users/";
    SQLiteDatabase db = null;
    DatabaseOpenHelper dbHelper = null;
    Cursor mCursor;
    String[] columns = new String[]{"_id", DatabaseOpenHelper.USERNAME, DatabaseOpenHelper.PASSWORD};
    String _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();
        mCursor = db.query(TABLE_NAME, columns, null, null, null, null,
                null);
        if(mCursor.moveToFirst()){
            _id = Integer.toString(mCursor.getInt(0));
        }
        tilFullname = (TextInputLayout) findViewById(R.id.tilFullname);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        points = (TextView) findViewById(R.id.points);
        username = (TextView) findViewById(R.id.username);
        progress = new ProgressDialog(UserInfo.this, R.style.CustomDialog);

        Bundle bundle = getIntent().getExtras();
        points.setText(Integer.toString(bundle.getInt("POINTS")));
        username.setText(bundle.getString("USERNAME"));
        tilFullname.getEditText().setText(bundle.getString("FULLNAME"));
        tilPassword.getEditText().setText(bundle.getString("PASSWORD"));
        apilink += username.getText().toString();
    }


    public void update(View v){
        String sFullName = tilFullname.getEditText().getText().toString().trim();
        String sPassword = tilPassword.getEditText().getText().toString().trim();
        String sPoints = points.getText().toString();
        String sUsername = username.getText().toString();

        tilFullname.setErrorEnabled(false);
        tilPassword.setErrorEnabled(false);
        if (sFullName.length() == 0) {
            tilFullname.setError("Please enter name");
        }
         else if (sPassword.length() == 0) {
            tilPassword.setError("Please enter a password");
        }
         else {
            tilFullname.setErrorEnabled(false);
            tilPassword.setErrorEnabled(false);
            new HttpTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apilink, sFullName, sPassword, sPoints, sUsername);
        }
    }


    public void logout(View v){
        dbHelper.deleteUser(_id);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class HttpTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute(){
            progress.setIndeterminate(true);
            progress.setMessage("Updating Information");
            progress.setCancelable(false);
            progress.show();
        }


        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection conn = null;
            JSONObject object = new JSONObject();
            try {

                object.put("fullName", params[1]);
                object.put("username", params[4]);
                object.put("password", params[2]);
                object.put("points", Integer.parseInt(params[3]));
            } catch (Exception e) {
                e.printStackTrace();
                return new String[]{getString(R.string.connectionError)};
            }
            try {
                conn = (HttpURLConnection) new URL(params[0]).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("PUT");
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(object.toString());
                out.flush();
                out.close();
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    return new String[]{getString(R.string.duplicateUserError)};
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                return new String[]{getString(R.string.connectionError)};
            } catch (IOException e1) {
                e1.printStackTrace();
                return new String[]{getString(R.string.connectionError)};
            }
            return new String[]{params[1], params[2]};
        }


        @Override
        protected void onPostExecute(String[] result) {
            if(result.length == 1){
                Toast.makeText(UserInfo.this, "An error occurred while updating user", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("FULLNAME", result[0]);
            intent.putExtra("PASSWORD", result[1]);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
