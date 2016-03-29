package aub.hopin;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;

public class NavigationSettings extends AppCompatActivity {

    private CheckBox tiltMapBox;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tiltMapBox = (CheckBox)findViewById(R.id.navigation_settings_tilt_map);
        seekBar = (SeekBar)findViewById(R.id.navigation_settings_seek_bar);

        tiltMapBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean b) {
                if (b) {
                    LocalUserPreferences.setBackgroundNavigationOn();
                } else {
                    LocalUserPreferences.setBackgroundNavigationOff();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int value, boolean fromUser) {
                final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
            }
            public void onStartTrackingTouch(SeekBar bar) { /* nop. */ }
            public void onStopTrackingTouch(SeekBar bar) { /* nop. */ }
        });
    }
}