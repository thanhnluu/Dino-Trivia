package cs_477.cs_477_course_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static cs_477.cs_477_course_project.DatabaseOpenHelper.TABLE_NAME;

public class MainActivity extends AppCompatActivity {

    Vibrator vib;
    ImageView loginImage;
    String apilink = "https://arcane-badlands-47658.herokuapp.com/api/users/", enterPass;
    TextInputLayout tilUsername, tilPassword;
    Animation animation;
    boolean animateOn = true;
    ProgressDialog progress;
    SQLiteDatabase db = null;
    DatabaseOpenHelper dbHelper = null;
    Cursor mCursor;
    String[] columns = new String[]{"_id", DatabaseOpenHelper.USERNAME, DatabaseOpenHelper.PASSWORD};
    boolean isAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = new ProgressDialog(MainActivity.this, R.style.CustomDialog);
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();
        mCursor = db.query(TABLE_NAME, columns, null, null, null, null,
                null);
        if (mCursor.moveToFirst()) {
            isAdded = true;
            enterPass = mCursor.getString(2);
            new HttpTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apilink + mCursor.getString(1), mCursor.getString(2));
        }
        loginImage = (ImageView) findViewById(R.id.loginImage);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        loginImage.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tilPassword.getEditText().setText("");
    }


    public void imageClicked(View v) {
        vib.vibrate(250);
        if (animateOn) {
            loginImage.clearAnimation();
            animateOn = false;
        } else {
            loginImage.startAnimation(animation);
            animateOn = true;
        }
    }


    public void createAccount(View v) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivityForResult(intent, 1);
    }


    public void login(View v) {
        String sUsername = tilUsername.getEditText().getText().toString().trim();
        String sPassword = tilPassword.getEditText().getText().toString().trim();

        tilUsername.setErrorEnabled(false);
        tilPassword.setErrorEnabled(false);
        if (sUsername.length() == 0) {
            tilUsername.setError("Please enter a username");
        } else if (sPassword.length() == 0) {
            tilPassword.setError("Please enter a password");
        } else {
            tilUsername.setErrorEnabled(false);
            tilPassword.setErrorEnabled(false);
            enterPass = sPassword;
            new HttpTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apilink + sUsername, sPassword);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Toast.makeText(this, data.getExtras().getString("MESSAGE"), Toast.LENGTH_SHORT).show();
        }
    }


    private void onFinishGetRequest(String result) {
        if (result.equals(getString(R.string.connectionError))) {
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.equals(getString(R.string.loginError))) {
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            JSONObject user = (new JSONObject(result));
            String fullname = user.getString("fullname");
            String password = user.getString("password");
            int points = user.getInt("points");
            String username = user.getString("username");
            Log.v("APP", "Got here3");
            if (!enterPass.equals(password)) {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ProfileScreen.class);
            Bundle bundle = new Bundle();
            bundle.putString("FULLNAME", fullname);
            bundle.putInt("POINTS", points);
            bundle.putString("USERNAME", username);
            bundle.putString("PASSWORD", password);
            intent.putExtras(bundle);
            if (!isAdded) {
                dbHelper.insertUser(username, password);
                isAdded = true;
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class HttpTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progress.setIndeterminate(true);
            progress.setMessage("Logging in");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer data = new StringBuffer();
            BufferedReader br = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) new
                        URL(params[0]).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    return getString(R.string.loginError);
                }
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String rawData;
                while ((rawData = br.readLine()) != null) {
                    data.append(rawData);
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                return getString(R.string.connectionError);
            } catch (IOException e1) {
                e1.printStackTrace();
                return getString(R.string.connectionError);
            } finally {
                if (br != null)
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return getString(R.string.connectionError);
                    }
            }
            return data.toString();
        }


        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            onFinishGetRequest(result);
        }

    }

    public void onBackPressed() {

    }

}
