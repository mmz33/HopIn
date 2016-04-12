package aub.hopin;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        switch (LocalUserPreferences.getUnitsSetting()) {
            case Metric:
                metric.setChecked(true);
                break;
            case Imperial:
                imperial.setChecked(true);
                break;
            case Automatic:
                automatic.setChecked(true);
                break;
        }

        unitsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int id) {
                if (automatic.getId() == id) {
                    LocalUserPreferences.setUnitsSetting(MeasurementUnitsSetting.Automatic);
                } else if (metric.getId() == id) {
                    LocalUserPreferences.setUnitsSetting(MeasurementUnitsSetting.Metric);
                } else if (imperial.getId() == id) {
                    LocalUserPreferences.setUnitsSetting(MeasurementUnitsSetting.Imperial);
                }
            }
        });
    }
}
