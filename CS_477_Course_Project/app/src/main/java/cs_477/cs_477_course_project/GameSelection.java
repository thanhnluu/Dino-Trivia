package cs_477.cs_477_course_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import info.hoang8f.widget.FButton;

public class GameSelection extends AppCompatActivity {

    Spinner category, difficulty;
    ArrayAdapter<String> category1, difficulty1;
    FButton startGame;
    Animation animation;
    ProgressDialog progress;
    String apiLink = "https://opentdb.com/api.php?amount=10&category=%d&difficulty=%s&type=multiple", selectedCategory, selectedDifficulty;
    String categories[];
    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);
        categories = getResources().getStringArray(R.array.categories);
        progress = new ProgressDialog(GameSelection.this, R.style.CustomDialog);

        startGame = (FButton) findViewById(R.id.startGame);
        animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        startGame.startAnimation(animation);
        builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Continue?");
        builder.setMessage("Once a game has started, it must be completed.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new HttpTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.format(apiLink, Arrays.asList(categories).indexOf(selectedCategory) + 9, selectedDifficulty.toLowerCase()));
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        dialog = builder.create();

        category = (Spinner) findViewById(R.id.category);
        category1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        category1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setPrompt("Choose a category");
        category.setAdapter(category1);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = category1.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        difficulty = (Spinner) findViewById(R.id.difficulty);
        difficulty1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.difficulty));
        difficulty1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficulty.setPrompt("Choose a difficulty");
        difficulty.setAdapter(difficulty1);
        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDifficulty = difficulty1.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void start_game(View v) {
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("POINTS", data.getExtras().getInt("POINTS"));
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void onFinishGetRequest(String result) {

        if (result.equals(getString(R.string.connectionError))) {
            Toast.makeText(this, "There was an error fetching the questions.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject data = (new JSONObject(result));
            if(data.getInt("response_code") != 0){
                Toast.makeText(this, "There was an error fetching the questions.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, GameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("JSONDATA", result);
            intent.putExtras(bundle);
            startActivityForResult(intent, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class HttpTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progress.setIndeterminate(true);
            progress.setMessage("Fetching questions");
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
                    return getString(R.string.connectionError);
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
}
