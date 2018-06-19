package cs_477.cs_477_course_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import info.hoang8f.widget.FButton;

public class GameActivity extends AppCompatActivity {

    JSONObject object;
    String data;
    int questionIndex; //keep track of position in question array
    int random; //random place to put the right answer
    int incorrectAnswer; //index of incorrect answers
    int points;
    JSONArray questionArray;
    FButton[] answerButtons;
    TextView question;
    Dialog correct, incorrect;
    MediaPlayer correctSound, incorrectSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        correctSound = MediaPlayer.create(this, R.raw.correct_sound_effect);
        incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound_effect);
        question = (TextView) findViewById(R.id.question);
        answerButtons = new FButton[4];
        answerButtons[0] = (FButton) findViewById(R.id.answer1);
        answerButtons[1] = (FButton) findViewById(R.id.answer2);
        answerButtons[2] = (FButton) findViewById(R.id.answer3);
        answerButtons[3] = (FButton) findViewById(R.id.answer4);
        correct = new Dialog(this);
        correct.requestWindowFeature(Window.FEATURE_NO_TITLE);
        correct.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        correct.setContentView(R.layout.answer_correct);
        correct.setCancelable(false);
        incorrect = new Dialog(this);
        incorrect.requestWindowFeature(Window.FEATURE_NO_TITLE);
        incorrect.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        incorrect.setContentView(R.layout.answer_incorrect);
        incorrect.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        data = bundle.getString("JSONDATA");
        try {
            object = new JSONObject(data);
            questionArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        questionIndex = 0;
        incorrectAnswer = 0;
        setUpQuestion();
    }

    public void answerChosen(View v) {
        if(questionIndex == 9){
            FButton button = (FButton)correct.findViewById(R.id.nextQuestion);
            button.setText("Finish");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("POINTS", points);
                    setResult(Activity.RESULT_OK, intent);
                    if (correct.isShowing())
                        correct.dismiss();
                    else
                        incorrect.dismiss();
                    finish();
                }
            });
            button = (FButton)incorrect.findViewById(R.id.nextQuestion);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("POINTS", points);
                    setResult(Activity.RESULT_OK, intent);
                    if (correct.isShowing())
                        correct.dismiss();
                    else
                        incorrect.dismiss();
                    finish();
                }
            });
            button.setText("Finish");
        }
        FButton button = (FButton) v;
        try {
            if (button.getText().toString().equals(Jsoup.parse(questionArray.getJSONObject(questionIndex).getString("correct_answer")).text().trim())) {
                points+=5;
                correct.show();
                correctSound.start();
                TextView textview = (TextView)correct.findViewById(R.id.textview);
                textview.setText("CORRECT\n\nPoints: "+ points);
            } else {
                incorrect.show();
                incorrectSound.start();
                TextView textview = (TextView)incorrect.findViewById(R.id.textview);
                textview.setText("INCORRECT\n\nPoints: "+ points);
            }
            questionIndex++;
            incorrectAnswer = 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void nextQuestion(View v) {
        if (correct.isShowing())
            correct.dismiss();
        else
            incorrect.dismiss();

        setUpQuestion();
    }

    public void setUpQuestion() {
        random = (int) (Math.random() * 4);

        try {
            answerButtons[random].setText(Jsoup.parse(questionArray.getJSONObject(questionIndex).getString("correct_answer")).text().trim());
            question.setText(Jsoup.parse(questionArray.getJSONObject(questionIndex).getString("question")).text().trim());
            for(int i = 0; i < 4; i++){
                if(i == random)
                    continue;
                else
                    answerButtons[i].setText(Jsoup.parse(questionArray.getJSONObject(questionIndex).getJSONArray("incorrect_answers").getString(incorrectAnswer++)).text().trim());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {

    }
}
