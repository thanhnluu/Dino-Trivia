package cs_477.cs_477_course_project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import link.fls.swipestack.SwipeStack;

public class RewardsDetails extends AppCompatActivity {

    ArrayList<String> data;
    SwipeStack swipeStack;
    SwipeStackAdapter adapter;
    int[] dinosaurs = new int[]{R.array.Parasaurolophus, R.array.Brachiosaurus, R.array.Sauropoda,
            R.array.Ankylosaurus, R.array.Apatosaurus, R.array.Allosaurus, R.array.Velociraptor,
            R.array.Stegosaurus, R.array.Triceratops, R.array.Tyrannosaurus_rex};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards_details);
        swipeStack = (SwipeStack) findViewById(R.id.swipeStack);
        data = new ArrayList<String>(Arrays.asList(getResources().getStringArray(dinosaurs[getIntent().getExtras().getInt("DINOSAUR")])));
        adapter = new SwipeStackAdapter(data);
        swipeStack.setAdapter(adapter);
        swipeStack.setListener(new SwipeStack.SwipeStackListener() {
            @Override
            public void onViewSwipedToLeft(int position) {

            }

            @Override
            public void onViewSwipedToRight(int position) {

            }

            @Override
            public void onStackEmpty() {
                swipeStack.resetStack();
            }
        });

    }

    public class SwipeStackAdapter extends BaseAdapter {

        private List<String> data;

        public SwipeStackAdapter(List<String> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.card, parent, false);
            }

            TextView textViewCard = (TextView) convertView.findViewById(R.id.textview);
            textViewCard.setText(data.get(position));

            return convertView;
        }
    }
}
