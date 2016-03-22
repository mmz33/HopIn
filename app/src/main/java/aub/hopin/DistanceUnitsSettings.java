package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class DistanceUnitsSettings extends AppCompatActivity {

    private RadioGroup unitsRadioGroup;
    private RadioButton automatic;
    private RadioButton imperial;
    private RadioButton metric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_units);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        unitsRadioGroup = (RadioGroup)findViewById(R.id.distance_units_radio_group);
        automatic = (RadioButton)findViewById(R.id.distance_units_automatic);
        imperial = (RadioButton)findViewById(R.id.distance_units_imperial);
        metric = (RadioButton)findViewById(R.id.distance_units_metric);

        unitsRadioGroup.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    if (automatic.getId() == v.getId()) {
                        String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
                        if (locale.equals("US")) {
                            LocalUserPreferences.setUnit(MeasurementUnits.Imperial);
                        } else {
                            LocalUserPreferences.setUnit(MeasurementUnits.Metric);
                        }
                    } else if (metric.getId() == v.getId()) {
                        LocalUserPreferences.setUnit(MeasurementUnits.Metric);
                    } else if (imperial.getId() == v.getId()) {
                        LocalUserPreferences.setUnit(MeasurementUnits.Imperial);
                    }
                }
            }
        );
    }
}