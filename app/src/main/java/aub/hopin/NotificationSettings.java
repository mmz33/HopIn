package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class NotificationSettings extends AppCompatActivity {

    private CheckBox backgroundNavigationBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backgroundNavigationBox = (CheckBox)findViewById(R.id.notifications_navigation);
        backgroundNavigationBox.setChecked(LocalUserPreferences.getBackgroundNavigation());

        backgroundNavigationBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean b) {
                LocalUserPreferences.setBackgroundNavigation(b);
            }
        });
    }
}