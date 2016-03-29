package aub.hopin;

import android.os.AsyncTask;
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

    private class AsyncUpdateCarInfo extends AsyncTask<Void, Void, Void> {
        private String typeText;
        private String countText;
        private String errorMessage;

        protected void onPreExecute() {
            super.onPreExecute();
            typeText = vehicleType.getText().toString();
            countText = carCapacity.getText().toString();
            if (countText.length() == 0)
                countText = "0";
            errorMessage = "";
        }

        protected Void doInBackground(Void... instances) {
            int count = Integer.parseInt(countText);
            if (count < 0) {
                errorMessage = "Passenger count cannot be negative!";
            } else if (count > 40) {
                errorMessage = "Exceeded maximum passenger count";
            } else {
                String email = ActiveUser.getActiveUserInfo().email;
                // TODO
                // Continue here
                // Send the vehicle data to the server.
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (errorMessage.length() > 0)
                errorText.setText(errorMessage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vehicleType = (EditText)findViewById(R.id.car_info_vehicle_type);
        carCapacity = (EditText)findViewById(R.id.car_info_passengers);
        okayButton  = (Button)findViewById(R.id.car_info_okay);
        errorText   = (TextView)findViewById(R.id.car_info_error_text);

        //TODO
        // Implement Vehicle Tracking and fix this afterwards.

        //UserSession session = UserSession.getActiveSession();
        //UserInfo info = session.getUserInfo();

        //this.vehicleType.setText(info.vehicleType);
        //this.carCapacity.setText("" + info.maximumPassengerCount);
        errorText.setText("");

        okayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AsyncUpdateCarInfo().execute();
            }
        });
    }
}
