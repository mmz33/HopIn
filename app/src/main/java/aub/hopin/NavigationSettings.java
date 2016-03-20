package aub.hopin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;

public class NavigationSettings extends AppCompatActivity {

    Switch tiltMapSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_settigns);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tiltMapSwitch = (Switch)findViewById(R.id.navigation_setting_switch);

        tiltMapSwitch.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int state = event.getButtonState();
                        if (state == 1) {
                            LocalUserPreferences.setTiltMapOn();
                            return true;
                        } else {
                            LocalUserPreferences.setTiltMapOff();
                            return false;
                        }
                    }
                }
        );
    }

}
