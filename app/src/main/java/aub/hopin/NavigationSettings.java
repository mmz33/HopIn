package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

public class NavigationSettings extends AppCompatActivity {

    private CheckBox tiltMapBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.tiltMapBox = (CheckBox)findViewById(R.id.navigation_settings_tilt_map);

        this.tiltMapBox.setOnTouchListener(
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int state = event.getButtonState();
                    if (state == 1) {
                        LocalUserPreferences.setTiltMapOn();
                    } else {
                        LocalUserPreferences.setTiltMapOff();
                    }
                    return true;
                }
            }
        );
    }
}