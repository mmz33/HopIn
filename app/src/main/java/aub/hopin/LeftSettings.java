package aub.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class LeftSettings extends AppCompatActivity {

    private Button distanceUnits;
    private Button notification;
    private Button navigation;
    private Button mapHistory;
    private Button showScaleOnMap;
    private Button termsAndPrivacy;
    private Button feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        distanceUnits = (Button)findViewById(R.id.left_settings_distance_units_button);
        notification = (Button)findViewById(R.id.left_settings_notification_button);
        navigation = (Button)findViewById(R.id.left_settings_navigation_button);
        mapHistory = (Button)findViewById(R.id.left_settings_map_history_button);
        showScaleOnMap = (Button)findViewById(R.id.left_settings_show_scale_on_map_button);
        termsAndPrivacy = (Button)findViewById(R.id.left_settings_terms_and_privacy_button);
        feedback = (Button)findViewById(R.id.left_settings_feedback_button);


        distanceUnits.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LeftSettings.this, DistanceUnitsSettings.class));
                    }
                }
        );

        notification.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LeftSettings.this, NotificationSettings.class));
                    }
                }
        );

        navigation.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LeftSettings.this, NavigationSettings.class));
                    }
                }
        );

        mapHistory.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LeftSettings.this, MapHistorySettings.class));
                    }
                }
        );

        feedback.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LeftSettings.this, FeedbackSettings.class));
                    }
                }
        );

    }

}
