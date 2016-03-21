package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

public class NotificationSettings extends AppCompatActivity {

    private CheckBox backgroundNavigationBox;
    private CheckBox requestDriverBox;
    private CheckBox requestPassengerBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.backgroundNavigationBox = (CheckBox)findViewById(R.id.notifications_navigation);
        this.requestDriverBox = (CheckBox)findViewById(R.id.notifications_receive_drivers);
        this.requestPassengerBox = (CheckBox)findViewById(R.id.notifications_receive_passengers);

        this.backgroundNavigationBox.setOnTouchListener(
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int state = event.getButtonState();
                    if(state == 1) {
                        LocalUserPreferences.setBackgroundNavigationOn();
                    } else {
                        LocalUserPreferences.setBackgroundNavigationOff();
                    }
                    return true;
                }
            }
        );
    }
}
