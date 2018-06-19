package cs_477.cs_477_course_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Rewards extends AppCompatActivity {
    ListView listView;
    CustomAdapter myAdapter;
    ArrayList<String> data;
    ArrayList<Integer> images;
    int points;
    int[] pointsNeeded = new int[]{50, 100, 300, 1200, 6000, 12000, 24000, 48000, 96000, 192000};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);
        listView = (ListView) findViewById(R.id.mylist);
        points = getIntent().getExtras().getInt("POINTS");
        data = new ArrayList<String>();
        images = new ArrayList<Integer>(Arrays.asList(new Integer[]{R.drawable.paras, R.drawable.brach, R.drawable.sauropo, R.drawable.ankylo, R.drawable.apatosa, R.drawable.allosaur, R.drawable.veloci, R.drawable.steg, R.drawable.tricera, R.drawable.trex}));
        data = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.dinosaurs)));
        myAdapter = new CustomAdapter(this, data, images);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (points < pointsNeeded[position]) {
                    Toast.makeText(Rewards.this, "You need " + NumberFormat.getInstance(Locale.US).format(pointsNeeded[position]) + " points to unlock this dinosaur", Toast.LENGTH_SHORT).show();
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("DINOSAUR", position);
                    Intent intent = new Intent(Rewards.this, RewardsDetails.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        List<String> data;
        List<Integer> images;

        public CustomAdapter(Context context, List<String> data, List<Integer> images) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.data = data;
            this.images = images;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.line, null);
            }

            TextView listText = (TextView) convertView.findViewById(R.id.listText);
            ImageView listImage = (ImageView) convertView.findViewById(R.id.listImage);
            listText.setText(data.get(position));
            listImage.setImageResource(images.get(position));
            return convertView;
        }

    }


}
