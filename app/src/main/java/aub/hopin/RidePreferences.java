package aub.hopin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.PortUnreachableException;

public class RidePreferences extends AppCompatActivity {

    private CheckBox withMen;
    private CheckBox withWomen;
    private CheckBox withStudents;
    private CheckBox withProfessors;
    private Spinner notificationRadius;
    private Button okayButton;
    private ProgressBar loading;
    private boolean currentlySendingPrefs;

    private class AsyncSendPrefs extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        private boolean prefsWithMen;
        private boolean prefsWithWomen;
        private boolean prefsWithStudents;
        private boolean prefsWithTeachers;
        private boolean success;
        private double radius;

        protected void onPreExecute() {
            super.onPreExecute();
            info = ActiveUser.getInfo();
            prefsWithMen = withMen.isChecked();
            prefsWithWomen = withWomen.isChecked();
            prefsWithStudents = withStudents.isChecked();
            prefsWithTeachers = withProfessors.isChecked();
            radius = Double.parseDouble(notificationRadius.getSelectedItem().toString());
            loading.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Void... params) {
            try {
                if (Server.sendRidePreferences(info.email, prefsWithMen, prefsWithWomen, prefsWithStudents, prefsWithTeachers, radius).equals("OK")) {
                    info.prefsWithStudents = prefsWithStudents;
                    info.prefsWithMen = prefsWithMen;
                    info.prefsWithWomen = prefsWithWomen;
                    info.prefsWithTeachers = prefsWithTeachers;
                    info.notificationRadius = radius;
                    success = true;
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                Toast.makeText(RidePreferences.this, "Failed to send preferences!", Toast.LENGTH_SHORT).show();
                currentlySendingPrefs = false;
            } else {
                finish();
            }
            loading.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        withMen = (CheckBox)findViewById(R.id.ride_preferences_men_check_box);
        withWomen = (CheckBox)findViewById(R.id.ride_preferences_women_check_box);
        withStudents = (CheckBox)findViewById(R.id.ride_preferences_students_check_box);
        withProfessors = (CheckBox)findViewById(R.id.ride_preferences_professors_check_box);
        notificationRadius = (Spinner)findViewById(R.id.ride_preferences_spinner);
        okayButton = (Button)findViewById(R.id.ride_preferences_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.distance, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationRadius.setAdapter(adapter);

        loading = (ProgressBar)findViewById(R.id.ride_preferences_loading);
        loading.setVisibility(View.GONE);

        UserInfo info = ActiveUser.getInfo();
        withMen.setChecked(info.prefsWithMen);
        withWomen.setChecked(info.prefsWithWomen);
        withStudents.setChecked(info.prefsWithStudents);
        withProfessors.setChecked(info.prefsWithTeachers);

        // Set the notification radius spinner appropriately.
        int integralRadius = (int)(0.5 + info.notificationRadius);
        int selection = 0;
        int totalItems = notificationRadius.getCount();
        notificationRadius.setSelection(selection);
        while (selection + 1 < totalItems && !notificationRadius.getSelectedItem().toString().equals("" + integralRadius)) {
            selection += 1;
            notificationRadius.setSelection(selection);
        }

        currentlySendingPrefs = false;

        okayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!currentlySendingPrefs) {
                    currentlySendingPrefs = true;
                    new AsyncSendPrefs().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
    }
}
