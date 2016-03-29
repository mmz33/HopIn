package aub.hopin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;


public class SignUp extends AppCompatActivity {

    private EditText firstNameBox;
    private EditText lastNameBox;
    private EditText emailBox;
    private EditText ageBox;

    private RadioButton optionMale;
    private RadioButton optionFemale;
    private RadioButton optionOther;

    private RadioButton optionDriver;
    private RadioButton optionPassenger;

    private Button signUp;
    private TextView errorText;

    private class AsyncSignUp extends AsyncTask<Void, Void, Void> {
        private String firstName;
        private String lastName;
        private String email;
        private int age;
        private UserGender gender;
        private UserMode mode;
        private boolean success;
        private String errorMessage;

        public AsyncSignUp(String firstName, String lastName, String email, int age, UserMode mode, UserGender gender) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.age = age;
            this.gender = gender;
            this.mode = mode;
            this.success = false;
            this.errorMessage = "";
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            String response = Server.signUp(firstName, lastName, email, age, mode, gender);
            if (response.equals("OK")) {
                success = true;
            } else {
                errorMessage = response;
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success) {
                Intent intent = new Intent(SignUp.this, SignUpConfirm.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                errorText.setText(errorMessage);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.firstNameBox = (EditText)findViewById(R.id.sign_up_first_name);
        this.lastNameBox = (EditText)findViewById(R.id.sign_up_last_name);
        this.emailBox = (EditText)findViewById(R.id.sign_up_email);
        this.ageBox = (EditText)findViewById(R.id.sign_up_age);
        this.optionMale = (RadioButton)findViewById(R.id.sign_up_male);
        this.optionFemale = (RadioButton)findViewById(R.id.sign_up_female);
        this.optionOther = (RadioButton)findViewById(R.id.sign_up_other);
        this.optionDriver = (RadioButton)findViewById(R.id.sign_up_driver);
        this.optionPassenger = (RadioButton)findViewById(R.id.sign_up_passenger);
        this.signUp = (Button)findViewById(R.id.sign_up_okay);
        this.errorText = (TextView)findViewById(R.id.sign_up_error_text);

        this.signUp.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    String firstName = SignUp.this.firstNameBox.getText().toString();
                    String lastName  = SignUp.this.lastNameBox.getText().toString();
                    String email     = SignUp.this.emailBox.getText().toString();
                    String age       = SignUp.this.ageBox.getText().toString();

                    UserGender gender = UserGender.Unspecified;
                    UserMode mode     = UserMode.Unspecified;

                    // Find the selected gender
                    if (SignUp.this.optionMale.isChecked())        gender = UserGender.Male;
                    else if (SignUp.this.optionFemale.isChecked()) gender = UserGender.Female;
                    else if (SignUp.this.optionOther.isChecked())  gender = UserGender.Other;

                    // Find the selected mode.
                    if (SignUp.this.optionDriver.isChecked())         mode = UserMode.DriverMode;
                    else if (SignUp.this.optionPassenger.isChecked()) mode = UserMode.PassengerMode;

                    // Find problems with the user input and report them.
                    if (firstName.length() == 0)
                        SignUp.this.errorText.setText("Please input first name.");
                    else if (lastName.length() == 0)
                        SignUp.this.errorText.setText("Please input last name.");
                    else if (email.length() == 0)
                        SignUp.this.errorText.setText("Please input email.");
                    else if (!email.endsWith("@aub.edu.lb") && !email.endsWith("@mail.aub.edu") && !email.endsWith("@aub.edu"))
                        SignUp.this.errorText.setText("Please use your university email.");
                    else if (age.length() == 0)
                        SignUp.this.errorText.setText("Please input age.");
                    else if (gender == UserGender.Unspecified)
                        SignUp.this.errorText.setText("Please specify gender.");
                    else if (mode == UserMode.Unspecified)
                        SignUp.this.errorText.setText("Please specify mode.");
                    else if (Integer.parseInt(age)  < 16 )
                        SignUp.this.errorText.setText("You must be 16 or older.");
                    else {
                        new AsyncSignUp(firstName, lastName, email, Integer.parseInt(age), mode, gender).execute();
                    }
                }
            });
    }
}
