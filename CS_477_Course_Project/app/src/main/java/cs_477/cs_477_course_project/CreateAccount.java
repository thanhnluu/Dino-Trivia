package cs_477.cs_477_course_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import info.hoang8f.widget.FButton;

public class CreateAccount extends AppCompatActivity {

    String apilink = "https://arcane-badlands-47658.herokuapp.com/api/users";
    TextInputLayout tilName, tilUsername, tilPassword, tilPassword2;
    FButton createAccount;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        tilName = (TextInputLayout) findViewById(R.id.tilName);
        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        tilPassword2 = (TextInputLayout) findViewById(R.id.tilPassword2);
        createAccount = (FButton) findViewById(R.id.createAccount);
        progress = new ProgressDialog(CreateAccount.this, R.style.CustomDialog);
    }


    public void createAccount(View v) {
        String sFullName = tilName.getEditText().getText().toString().trim();
        String sUsername = tilUsername.getEditText().getText().toString().trim();
        String sPassword = tilPassword.getEditText().getText().toString().trim();
        String sPassword2 = tilPassword2.getEditText().getText().toString().trim();

        tilName.setErrorEnabled(false);
        tilUsername.setErrorEnabled(false);
        tilPassword.setErrorEnabled(false);
        tilPassword2.setErrorEnabled(false);
        if (sFullName.length() == 0) {
            tilName.setError("Please enter name");
        } else if (sUsername.length() == 0) {
            tilUsername.setError("Please enter a username");
        } else if (sPassword.length() == 0) {
            tilPassword.setError("Please enter a password");
        } else if (!sPassword.equals(sPassword2)) {
            tilPassword2.setError("Passwords do not match");
        } else {
            tilName.setErrorEnabled(false);
            tilUsername.setErrorEnabled(false);
            tilPassword.setErrorEnabled(false);
            tilPassword2.setErrorEnabled(false);
            new HttpTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apilink, sFullName, sUsername, sPassword);
        }
    }


    private class HttpTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            progress.setIndeterminate(true);
            progress.setMessage("Creating account");
            progress.setCancelable(false);
            progress.show();
        }


        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null;
            JSONObject object = new JSONObject();
            try {
                object.put("fullName", params[1]);
                object.put("username", params[2]);
                object.put("password", params[3]);
            } catch (Exception e) {
                e.printStackTrace();
                return getString(R.string.connectionError);
            }
            try {
                conn = (HttpURLConnection) new URL(params[0]).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("POST");
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(object.toString());
                out.flush();
                out.close();
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    return getString(R.string.duplicateUserError);
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                return getString(R.string.connectionError);
            } catch (IOException e1) {
                e1.printStackTrace();
                return getString(R.string.connectionError);
            }
            return getString(R.string.createUserSuccess);
        }


        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent();
            intent.putExtra("MESSAGE", result);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }


    public void onBackPressed() {
        finish();
    }
}
