package aub.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class RightSettings extends AppCompatActivity {

    private Button schedule;
    private Button contactInfo;
    private Button carInfo;
    private Button profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_right_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        schedule = (Button)findViewById(R.id.right_settings_schedule_button);
        contactInfo = (Button)findViewById(R.id.right_settings_contact_info_button);
        carInfo = (Button)findViewById(R.id.right_settings_car_info_button);
        profile = (Button)findViewById(R.id.right_settings_profile_button);

        schedule.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(RightSettings.this, ScheduleSettings.class));
                    }
                }
        );

        contactInfo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(RightSettings.this, ContactInfoSettings.class));
                    }
                }
        );

        carInfo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(RightSettings.this, CarInfoSettings.class));
                    }
                }
        );

        profile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(RightSettings.this, ProfileSettings.class));
                    }
                }
        );
    }

}
