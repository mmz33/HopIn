package aub.hopin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;

public class NotificationSettings extends AppCompatActivity {

    Switch backgroundNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backgroundNavigation = (Switch)findViewById(R.id.notification_background_navigation_switch);

        backgroundNavigation.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int state = event.getButtonState();
                        if(state == 1) {
                            LocalUserPreferences.setBackgroundNavigationOn();
                            return true;
                        }
                        else {
                            LocalUserPreferences.setBackgroundNavigationOff();
                            return false;
                        }
                    }
                }
        );

    }

}
