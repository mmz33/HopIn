package aub.hopin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Settings extends AppCompatActivity {

    private RadioGroup unitsRadioGroup;
    private RadioButton automatic;
    private RadioButton imperial;
    private RadioButton metric;

    private CheckBox backgroundNavigationBox;

    private RadioGroup scaleRadioGroup;
    private RadioButton  zooming;
    private RadioButton always;

    private CheckBox tiltMapBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

        backgroundNavigationBox = (CheckBox)findViewById(R.id.notifications_navigation);
        backgroundNavigationBox.setChecked(LocalUserPreferences.getBackgroundNavigation());

        backgroundNavigationBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean b) {
                LocalUserPreferences.setBackgroundNavigation(b);
            }
        });

        scaleRadioGroup = (RadioGroup)findViewById(R.id.show_scale_on_map_radio_group);
        zooming = (RadioButton)findViewById(R.id.show_scale_when_zooming);
        always = (RadioButton)findViewById(R.id.show_scale_always);

        scaleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(zooming.getId() == checkedId) {

                }
                else if(always.getId() == checkedId) {

                }
            }
        });

        tiltMapBox = (CheckBox)findViewById(R.id.map_display_settings_tilt_map);
        tiltMapBox.setChecked(LocalUserPreferences.getTiltMap());

        tiltMapBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean b) {
                LocalUserPreferences.setTiltMap(b);
            }
        });
    }
}
