package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class CarSettings extends AppCompatActivity {

    private EditText vehicleType;
    private EditText carCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.vehicleType = (EditText)findViewById(R.id.car_vehicle_type_edit_text);
        this.carCapacity = (EditText)findViewById(R.id.car_car_capacity_edit_text);

        this.vehicleType.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        String type = CarSettings.this.vehicleType.getText().toString();
                        Server.sendVehicleType(UserSession.getActiveSession(), type);
                    }
                }
        );

        this.carCapacity.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        int capacity = Integer.parseInt(CarSettings.this.carCapacity.getText().toString());
                        Server.sendVehiclePassengerCount(UserSession.getActiveSession(), capacity);
                    }
                }
        );

    }

}
