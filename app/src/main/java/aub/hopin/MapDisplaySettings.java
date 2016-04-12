package aub.hopin;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.CompoundButton;


public class MapDisplaySettings extends AppCompatActivity {

    private CheckBox tiltMapBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tiltMapBox = (CheckBox)findViewById(R.id.map_display_settings_tilt_map);
        tiltMapBox.setChecked(LocalUserPreferences.getTiltMap());

        tiltMapBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean b) {
                LocalUserPreferences.setTiltMap(b);
            }
        });
    }
}
