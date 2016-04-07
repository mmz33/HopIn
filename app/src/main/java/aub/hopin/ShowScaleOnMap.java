package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ShowScaleOnMap extends AppCompatActivity{

    private RadioGroup scaleRadioGroup;
    private RadioButton  zooming;
    private RadioButton always;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_scale_on_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }
}
