package aub.hopin;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class CarInfoSettings extends AppCompatActivity {

    private EditText carCapacity;
    private Button okayButton;
    private AutoCompleteTextView vehicleType;
    private AutoCompleteTextView vehicleColor;
    private boolean currentlySendingInfo;
    private ProgressBar loading;

    /**
     * The acknowledged car makes that the
     * user can choose from in the auto-complete view.
     */
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

    /**
     * The acknowledged colors that the user can
     * choose from the in the auto-complete view.
     */
    private static final String[] COLORS = new String[] {
            "Red",     "Dark Green",     "Green",     "Blue",     "Light Blue",
            "Purple",     "Light Purple",     "Pink",     "Light Pink",     "Dark Blue",
            "Brown",     "Beige",     "Silver",     "Black",     "White",
            "Orange",     "Yellow",     "Grey",     "Dark Grey"
    };

    /**
     * Determines if make is a valid acknowledged car make.
     *
     * @param make  the car make
     * @return      true if the car make is acknowledged, false otherwise
     */
    private static boolean validCarMake(String make) {
        // Linear search is good enough since the
        // makes are going to be very small anyway.
        for (int i = 0; i < CAR_MAKES.length; ++i) {
            if (CAR_MAKES[i].equals(make)) return true;
        }
        return false;
    }

    /**
     * Determines if color is a valid acknowledged car color.
     *
     * @param color the car color
     * @return      true if the color is acknowledged
     */
    private static boolean validCarColor(String color) {
        for (int i = 0; i < COLORS.length; ++i) {
            if (COLORS[i].equals(color)) return true;
        }
        return false;
    }

    /**
     * Determines if a car capacity is valid.
     *
     * @param capacity  the car capacity
     * @return          true if it's valid, false otherwise
     */
    private static boolean validCarCapacity(int capacity) {
        return 2 <= capacity && capacity <= 9;
    }

    private class AsyncUpdateCarInfo extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        private String make;
        private String color;
        private int capacity;
        private boolean success;

        public AsyncUpdateCarInfo(String make, String color, int capacity) {
            this.make = make;
            this.color = color;
            this.capacity = capacity;
            this.info = ActiveUser.getInfo();
            this.success = false;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Void... instances) {
            try {
                String response = Server.sendVehicleInfo(info.email, make, color, capacity);
                if (response.equals("OK")) {
                    info.vehicle.capacity = capacity;
                    info.vehicle.color = color;
                    info.vehicle.make = make;
                    info.vehicle.ownerEmail = info.email;
                    success = true;
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loading.setVisibility(View.GONE);
            if (success) {
                finish();
            } else {
                Toast.makeText(CarInfoSettings.this, "Failed to send vehicle info!", Toast.LENGTH_SHORT).show();
                vehicleType.setText(info.vehicle.make);
                vehicleColor.setText(info.vehicle.color);
                carCapacity.setText("" + info.vehicle.capacity);
                currentlySendingInfo = false;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentlySendingInfo = false;

        vehicleType = (AutoCompleteTextView)findViewById(R.id.car_info_vehicle_type_auto_complete_text);
        vehicleColor = (AutoCompleteTextView)findViewById(R.id.car_info_colors_auto_complete_text);
        carCapacity = (EditText)findViewById(R.id.car_info_passengers);
        okayButton  = (Button)findViewById(R.id.car_info_okay);

        loading = (ProgressBar)findViewById(R.id.car_info_loading);
        loading.setVisibility(View.GONE);

        final ArrayAdapter<String> vehicleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, CAR_MAKES);
        AutoCompleteTextView cars = vehicleType;
        cars.setAdapter(vehicleAdapter);

        final ArrayAdapter<String> carAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, COLORS);
        AutoCompleteTextView colors = vehicleColor;
        colors.setAdapter(carAdapter);

        UserInfo info = ActiveUser.getInfo();

        if (info.vehicle != null) {
            vehicleType.setText(info.vehicle.make);
            vehicleColor.setText(info.vehicle.color);
            carCapacity.setText("" + info.vehicle.capacity);
        } else {
            vehicleType.setText("");
            vehicleColor.setText("");
            carCapacity.setText("");
        }

        okayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                if (!currentlySendingInfo) {
                    String make = vehicleType.getEditableText().toString();
                    String color = vehicleColor.getEditableText().toString();
                    String capacityStr = carCapacity.getEditableText().toString();

                    if (!validCarMake(make)) {
                        Toast.makeText(CarInfoSettings.this, "Invalid make!", Toast.LENGTH_SHORT).show();
                    } else if (!validCarColor(color)) {
                        Toast.makeText(CarInfoSettings.this, "Invalid color!", Toast.LENGTH_SHORT).show();
                    } else if (capacityStr.length() == 0) {
                        Toast.makeText(CarInfoSettings.this, "Invalid capacity!", Toast.LENGTH_SHORT).show();
                    } else {
                        int capacity = Integer.parseInt(capacityStr);
                        if (!validCarCapacity(capacity)) {
                            Toast.makeText(CarInfoSettings.this, "Capacity out of range!", Toast.LENGTH_SHORT).show();
                        } else {
                            currentlySendingInfo = true;
                            new AsyncUpdateCarInfo(make, color, capacity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }
                }
            }
        });
    }
}
