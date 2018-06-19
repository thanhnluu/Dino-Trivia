package cs_477.cs_477_course_project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }


    public void back(View v){
        finish();
    }


    public void onBackPressed() {
        finish();
    }
}
