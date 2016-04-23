package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.net.PortUnreachableException;

public class RidePreferences extends AppCompatActivity {

    private CheckBox withMen;
    private CheckBox withWomen;
    private CheckBox withStudents;
    private CheckBox withProfessors;
    private Spinner notificationRadius;

    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        withMen = (CheckBox)findViewById(R.id.ride_preferences_men_check_box);
        withWomen = (CheckBox)findViewById(R.id.ride_preferences_women_check_box);
        withStudents = (CheckBox)findViewById(R.id.ride_preferences_students_check_box);
        withProfessors = (CheckBox)findViewById(R.id.ride_preferences_professors_check_box);

        notificationRadius = (Spinner)findViewById(R.id.ride_preferences_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.distance, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationRadius.setAdapter(adapter);

        loading = (ProgressBar)findViewById(R.id.ride_preferences_loading);
        loading.setVisibility(View.GONE);
    }
}
