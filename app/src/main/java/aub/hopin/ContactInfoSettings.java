package aub.hopin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class ContactInfoSettings extends AppCompatActivity {

    private EditText phoneNumberBox;
    private Button okayButton;

    private class AsyncContactInfoUpdate extends AsyncTask<Void, Void, Void> {
        private String phoneNumber;
        private boolean success;

        protected void onPreExecute() {
            super.onPreExecute();
            phoneNumber = phoneNumberBox.getText().toString();
            success = false;
        }

        protected Void doInBackground(Void... params) {
            String email = ActiveUser.getEmail();
            if (Server.sendPhoneNumber(email, phoneNumber).equals("OK")) {
                ActiveUser.getActiveUserInfo().phoneNumber = phoneNumber;
                success = true;
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success)
                phoneNumberBox.setText(phoneNumber);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        phoneNumberBox = (EditText)findViewById(R.id.contact_info_phone_number);
        okayButton = (Button)findViewById(R.id.contact_info_okay);

        phoneNumberBox.setText(ActiveUser.getActiveUserInfo().phoneNumber);

        okayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AsyncContactInfoUpdate().execute();
            }
        });
    }
}
