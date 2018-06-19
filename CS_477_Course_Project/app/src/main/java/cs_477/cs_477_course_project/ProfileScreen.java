package cs_477.cs_477_course_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import info.hoang8f.widget.FButton;

public class ProfileScreen extends AppCompatActivity {

    TextView welcome, txtPoints;
    FButton newGame, userProfile, help, rewards;
    ImageView imageView;
    String fullname, username, password;
    int points;
    String apilink = "https://arcane-badlands-47658.herokuapp.com/api/users/";
    ProgressDialog progress;
    MediaPlayer dinosaurRoar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        dinosaurRoar = MediaPlayer.create(this, R.raw.funny_sound);
        Bundle bundle = getIntent().getExtras();
        welcome = (TextView) findViewById(R.id.welcome);
        txtPoints = (TextView) findViewById(R.id.points);
        newGame = (FButton) findViewById(R.id.newGame);
        userProfile = (FButton) findViewById(R.id.userProfile);
        help = (FButton) findViewById(R.id.help);
        rewards = (FButton) findViewById(R.id.rewards);
        imageView = (ImageView) findViewById(R.id.imageView);
        progress = new ProgressDialog(ProfileScreen.this, R.style.CustomDialog);

        fullname = bundle.getString("FULLNAME");
        points = bundle.getInt("POINTS");
        username = bundle.getString("USERNAME");
        password = bundle.getString("PASSWORD");

        welcome.setText("Welcome " + fullname);
        txtPoints.setText("Points: " + points);
        apilink += username;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(progress.isShowing())
            progress.dismiss();
    }

    public void roar(View v){
        dinosaurRoar.start();
    }


    public void new_game(View v) {
        Intent intent = new Intent(this, GameSelection.class);
        startActivityForResult(intent, 1);
    }


    public void user_profile(View v) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, UserInfo.class);
        bundle.putString("USERNAME", username);
        bundle.putInt("POINTS", points);
        bundle.putString("FULLNAME", fullname);
        bundle.putString("PASSWORD", password);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
    }


    public void help(View v) {
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }


    public void rewards(View v) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, Rewards.class);
        bundle.putInt("POINTS", points);
        intent.putExtras(bundle);
        progress.setIndeterminate(true);
        progress.setMessage("Loading rewards");
        progress.setCancelable(false);
        progress.show();
        startActivity(intent);
    }

    private class HttpTask extends AsyncTask<String, Void, String[]> {

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
            return new String[]{params[1], params[3]};
        }


        @Override
        protected void onPostExecute(String[] result) {
            if (result.length == 1) {
                Toast.makeText(ProfileScreen.this, "An error occurred while updating user points", Toast.LENGTH_SHORT).show();
                return;
            }
            points = Integer.parseInt(result[1]);
            txtPoints.setText("Points: " + points);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK)
            new HttpTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apilink, fullname, password, Integer.toString(points + data.getExtras().getInt("POINTS")), username);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            fullname = data.getExtras().getString("FULLNAME");
            password = data.getExtras().getString("PASSWORD");
            welcome.setText("Welcome " + fullname);
        }
    }

    public void onBackPressed() {

    }
}
