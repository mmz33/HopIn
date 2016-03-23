package aub.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import java.util.ArrayList;
import java.util.Arrays;

public class Map2Activity extends Activity{
    /*private SwipeDetector swipeDetector;
    private ListView rightListView;
    private ListView leftListView;

    private ArrayList<String> rightItems;
    private ArrayList<String> leftItems;

    private ArrayAdapter<String> rightAdapter;
    private ArrayAdapter<String> leftAdapter;*/

    private Button leftButton;
    private Button rightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        /*rightListView = (ListView)findViewById(R.id.map2_right_list_view);
        leftListView = (ListView)findViewById(R.id.map2_left_list_view);

        String[] rItems = {"Schedule", "Contact Info", "Car Info", "Profile"};
        String[] lItems = {"Distance Units", "Notification", "Navigation", "Map History", "Map History", "Show Scale On Map",
                                "Terms and Privacy", "Feedback"};

        rightItems = new ArrayList<>(Arrays.asList(rItems));
        leftItems = new ArrayList<>(Arrays.asList(lItems));

        rightAdapter = new ArrayAdapter<String>(this, R.layout.right_list, R.id.right_text);
        leftAdapter = new ArrayAdapter<String>(this, R.layout.left_list, R.id.left_text);

        rightListView.setAdapter(rightAdapter);
        leftListView.setAdapter(leftAdapter);

        rightListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                        if (swipeDetector.swipeDetected()) {
                            if (swipeDetector.getAction() == Action.LR) {
                                Toast.makeText(getApplicationContext(), "Left to Right", Toast.LENGTH_LONG).show();
                            }
                            if (swipeDetector.getAction() == Action.RL) {
                                Toast.makeText(getApplicationContext(), "Right to Left", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
        );*/

        leftButton = (Button)findViewById(R.id.map2_left_settings_button);
        rightButton = (Button)findViewById(R.id.map2_right_settings_button);

        leftButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Map2Activity.this, LeftSettings.class));
                    }
                }
        );

        rightButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Map2Activity.this, RightSettings.class));
                    }
                }
        );
    }
}
