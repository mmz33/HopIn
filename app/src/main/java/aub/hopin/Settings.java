package aub.hopin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Settings extends AppCompatActivity{

    private ArrayAdapter<String> adapter;
    private List<String> settings;
    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String items[] = {"Profile", "Schedule", "Contact Info", "Car Info","Distance units", "Notification", "Navigation",
                          "Show scale on Map", "Terms and Privacy", "Feedback"};

        settings = new ArrayList<String>(Arrays.asList(items));
        list = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, R.layout.list_row, settings);
        list.setAdapter(adapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0)
                            startActivity(new Intent(Settings.this, ProfileSettings.class));
                        else if(position == 1)
                            startActivity(new Intent(Settings.this, ScheduleSettings.class));
                        else if(position == 2)
                            startActivity(new Intent(Settings.this, ContactInfoSettings.class));
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_page, menu);
        return true;
    }
}
