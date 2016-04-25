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

    private CheckBox tiltBox;
    private CheckBox zoomBox;
    private CheckBox rotBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tiltBox = (CheckBox)findViewById(R.id.map_display_settings_tilt_map);
        zoomBox = (CheckBox)findViewById(R.id.map_display_settings_zoom_map);
        rotBox  = (CheckBox)findViewById(R.id.map_display_settings_rotate_map);

        tiltBox.setChecked(LocalUserPreferences.getTiltMap());
        zoomBox.setChecked(LocalUserPreferences.getZoomMap());
        rotBox.setChecked(LocalUserPreferences.getRotateMap());

        tiltBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean b) {
                LocalUserPreferences.setTiltMap(b);
            }
        });
        zoomBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean b) {
                LocalUserPreferences.setZoomMap(b);
            }
        });
        rotBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean b) {
                LocalUserPreferences.setRotateMap(b);
            }
        });
    }
}
