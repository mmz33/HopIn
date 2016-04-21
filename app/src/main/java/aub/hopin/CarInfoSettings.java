package aub.hopin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

public class CarInfoSettings extends AppCompatActivity {

    private EditText carCapacity;
    private Button okayButton;
    private TextView errorText;
    private AutoCompleteTextView vehicleType;
    private AutoCompleteTextView vehicleColor;

    private static final String[] CAR_MAKES = new String[] {
            "Acura",     "Alfa Romeo",     "AMC",     "Ariel",     "Aston Martin",
            "Audi",     "Austin Healey",     "Bentley",     "BMW",     "Bugatti",
            "Buick",     "Cadillac",     "Callaway",     "Caterham",     "Chevrolet",
            "Chrysler",     "Citroen",     "Daewoo",     "Daihatsu",     "Datsun",
            "De Tomaso",     "Dodge",     "Eagle",     "Ferrari",     "Fiat",
            "Fisker",     "Ford",     "Geo",     "GMC",     "Holden",
            "Honda",     "Hummer",     "Hyundai",     "Infiniti",     "Isuzu",
            "Jaguar",     "Jeep",     "Kia",     "Koenigsegg",     "Lamborghini",
            "Lancia",     "Land Rover",     "Lexus",     "Lincoln",     "Lotus",
            "Maserati",     "Maybach",     "Mazda",     "McLaren",     "Mercedes",
            "Mercury",     "MG",     "Mini",     "Mitsubishi",     "Morgan",
            "Mosler",     "Nissan",     "Noble",     "Oldsmobile",     "Opel",
            "Pagani",     "Peugeot",     "Plymouth",     "Pontiac",     "Porsche",
            "Proton",     "Ram",     "Range Rover",     "Renault",     "Rolls-Royce",
            "Rossion",     "Saab",     "Saleen",     "Saturn",     "Scion",
            "Seat",     "Shelby",     "Skoda",     "Smart",     "Ssangyong",
            "Subaru",     "Suzuki",     "Tesla",     "Toyota",     "Triumph",
            "Vauxhall",     "VW",     "Volvo",     "Westfield"
    };

    private static final String[] COLORS = new String[] {
            "Red",     "Dark Green",     "Green",     "Blue",     "Light Blue",
            "Purple",     "Light Purple",     "Pink",     "Light Pink",     "Dark Blue",
            "Brown",     "Beige",     "Silver",     "Black",     "White",
            "Orange",     "Yellow",     "Grey",     "Dark Grey"
    };

    private static boolean validCarMake(String make) {
        // Linear search is good enough since the
        // makes are going to be very small anyway.
        for (int i = 0; i < CAR_MAKES.length; ++i) {
            if (CAR_MAKES[i].equals(make)) return true;
        }
        return false;
    }

    private static boolean validCarColor(String color) {
        for (int i = 0; i < COLORS.length; ++i) {
            if (COLORS[i].equals(color)) return true;
        }
        return false;
    }

    private static boolean validCarCapacity(int capacity) {
        return 2 <= capacity && capacity <= 9;
    }

    private class AsyncUpdateCarInfo extends AsyncTask<Void, Void, Void> {
        private String typeText;
        private String errorMessage;

        private String makeText;
        private String colorText;
        private int capacity;

        public AsyncUpdateCarInfo(String make, String color, int capacity) {
            makeText = make;
            colorText = color;
            this.capacity = capacity;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            errorMessage = "";
        }

        protected Void doInBackground(Void... instances) {
            String email = ActiveUser.getInfo().email;
            // TODO
            // Continue here
            // Send the vehicle data to the server.
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

        vehicleType = (AutoCompleteTextView)findViewById(R.id.car_info_vehicle_type_auto_complete_text);
        vehicleColor = (AutoCompleteTextView)findViewById(R.id.car_info_colors_auto_complete_text);
        carCapacity = (EditText)findViewById(R.id.car_info_passengers);
        okayButton  = (Button)findViewById(R.id.car_info_okay);
        errorText   = (TextView)findViewById(R.id.car_info_error_text);

        final ArrayAdapter<String> vehicleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, CAR_MAKES);
        AutoCompleteTextView cars = vehicleType;
        cars.setAdapter(vehicleAdapter);

        // TODO
        // Get the current vehicle color of the user.
        // Get the current vehicle make of the user as well.

        final ArrayAdapter<String> carAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, COLORS);
        AutoCompleteTextView colors = vehicleColor;
        colors.setAdapter(carAdapter);

        // TODO
        // Set the boxes to their correct values.
        //UserSession session = UserSession.getActiveSession();
        //UserInfo info = session.getUserInfo();

        //this.vehicleType.setText(info.vehicleType);
        //this.carCapacity.setText("" + info.maximumPassengerCount);
        errorText.setText("");

        okayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String make = vehicleType.getEditableText().toString();
                String color = vehicleColor.getEditableText().toString();
                int capacity = Integer.parseInt(carCapacity.getEditableText().toString());

                if (validCarMake(make)) {
                    if (validCarColor(color)) {
                        if (validCarCapacity(capacity)) {
                            new AsyncUpdateCarInfo(make, color, capacity).execute();
                        } else {
                            errorText.setText("Invalid car capacity.");
                        }
                    } else {
                        errorText.setText("Invalid car color.");
                    }
                } else {
                    errorText.setText("Invalid car make.");
                }
            }
        });
    }
}
