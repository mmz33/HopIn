package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

public class CarInfoSettings extends AppCompatActivity {

    private EditText vehicleType;
    private EditText carCapacity;
    private Button okayButton;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.vehicleType = (EditText)findViewById(R.id.car_info_vehicle_type);
        this.carCapacity = (EditText)findViewById(R.id.car_info_passengers);
        this.okayButton  = (Button)findViewById(R.id.car_info_okay);
        this.errorText   = (TextView)findViewById(R.id.car_info_error_text);

        UserSession session = UserSession.getActiveSession();
        UserInfo info = session.getUserInfo();

        this.vehicleType.setText(info.vehicleType);
        this.carCapacity.setText("" + info.maximumPassengerCount);
        this.errorText.setText("");

        this.okayButton.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    String type = CarInfoSettings.this.vehicleType.getText().toString();
                    String countStr = CarInfoSettings.this.carCapacity.getText().toString();

                    if (type.length() == 0) { type = "default"; }
                    if (countStr.length() == 0) { countStr = "0"; }

                    int count = Integer.parseInt(countStr);
                    if (count < 0) {
                        CarInfoSettings.this.errorText.setText("Passenger count cannot be negative!");
                    } else if (count > 40) {
                        CarInfoSettings.this.errorText.setText("Exceeded maximum passenger count");
                    } else {
                        UserSession session = UserSession.getActiveSession();
                        //Server.sendVehiclePassengerCount(session, count);
                        //Server.sendVehicleType(session, type);

                        session.getUserInfo().maximumPassengerCount = count;
                        session.getUserInfo().vehicleType = type;
                    }
                }
            }
        );
    }
}
