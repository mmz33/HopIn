package aub.hopin;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ContactInfoSettings extends AppCompatActivity {

    private EditText phoneNumberBox;
    private EditText addressBox;
    private EditText postOfficeBox;
    private CheckBox showPhoneBox;
    private CheckBox showAddressBox;
    private Button okayButton;
    private ProgressBar spinner;
    private boolean currentlyUpdating;

    private class AsyncContactInfoUpdate extends AsyncTask<Void, Void, Void> {
        private String phoneNumber;
        private String address;
        private String poBox;
        private boolean showPhone;
        private boolean showAddress;
        private boolean success;
        private UserInfo info;

        protected void onPreExecute() {
            super.onPreExecute();
            info = ActiveUser.getInfo();
            phoneNumber = phoneNumberBox.getText().toString();
            address = addressBox.getText().toString();
            poBox = postOfficeBox.getText().toString();
            showPhone = showPhoneBox.isChecked();
            showAddress = showAddressBox.isChecked();
            success = false;
            spinner.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Void... params) {
            try {
                HashMap<String, String> data = new HashMap<>();
                data.put("phone", phoneNumber);
                data.put("address", address);
                data.put("pobox", poBox);
                data.put("showphone", showPhone ? "1" : "0");
                data.put("showaddress", showAddress ? "1" : "0");
                String response = Server.sendUserInfoBundle(info.email, data);
                if (response.equals("OK")) {
                    info.phoneNumber = phoneNumber;
                    info.address = address;
                    info.poBox = poBox;
                    info.showingPhone = showPhone;
                    info.showingAddress = showAddress;
                    success = true;
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                phoneNumberBox.setText(info.phoneNumber);
                addressBox.setText(info.address);
                postOfficeBox.setText(info.poBox);
                showPhoneBox.setChecked(info.showingPhone);
                showAddressBox.setChecked(info.showingAddress);
                Toast.makeText(ContactInfoSettings.this, "Failed to send contact info!", Toast.LENGTH_SHORT).show();
                currentlyUpdating = false;
            } else {
                finish();
            }
            spinner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        phoneNumberBox = (EditText)findViewById(R.id.contact_info_phone_number);
        addressBox = (EditText)findViewById(R.id.contact_info_address);
        postOfficeBox = (EditText)findViewById(R.id.contact_info_post_office);
        showPhoneBox = (CheckBox)findViewById(R.id.contact_info_show_phone);
        showAddressBox = (CheckBox)findViewById(R.id.contact_info_show_address);
        okayButton = (Button)findViewById(R.id.contact_info_okay);
        spinner = (ProgressBar)findViewById(R.id.contact_info_loading);

        UserInfo info = ActiveUser.getInfo();
        currentlyUpdating = false;

        phoneNumberBox.setText(info.phoneNumber);
        addressBox.setText(info.address);
        postOfficeBox.setText(info.poBox);
        showPhoneBox.setChecked(info.showingPhone);
        showAddressBox.setChecked(info.showingAddress);
        spinner.setVisibility(View.GONE);

        okayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (!currentlyUpdating) {
                    currentlyUpdating = true;
                    new AsyncContactInfoUpdate().execute();
                }
            }
        });
    }
}
