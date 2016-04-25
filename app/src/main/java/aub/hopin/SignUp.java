package aub.hopin;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;


public class SignUp extends AppCompatActivity {

    private EditText firstNameBox;
    private EditText lastNameBox;
    private EditText emailBox;
    private EditText ageBox;

    private RadioButton optionMale;
    private RadioButton optionFemale;
    private RadioButton optionOther;

    private RadioButton optionStudent;
    private RadioButton optionProfessor;

    private Button signUp;
    private ProgressBar loading;
    private boolean currentlySigningUp;

    private class AsyncSignUp extends AsyncTask<Void, Void, Void> {
        private String firstName;
        private String lastName;
        private String email;
        private int age;
        private UserGender gender;
        private UserMode mode;
        private UserRole role;
        private boolean success;
        private String customErrorMessage;

        public AsyncSignUp(String firstName, String lastName, String email, int age, UserMode mode, UserGender gender, UserRole role) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.age = age;
            this.gender = gender;
            this.mode = mode;
            this.role = role;
            this.success = false;
            customErrorMessage = "";
        }

        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Void... params) {
            try {
                String response = Server.signUp(firstName, lastName, email, age, mode, gender, role);
                if (response.equals("OK")) {
                    success = true;
                } else {
                    customErrorMessage = response;
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loading.setVisibility(View.GONE);
            if (success) {
                Intent intent = new Intent(SignUp.this, SignUpConfirm.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                if (customErrorMessage.length() == 0) {
                    customErrorMessage = "Failed to connect to server!";
                }
                Toast.makeText(SignUp.this, customErrorMessage, Toast.LENGTH_SHORT).show();
                currentlySigningUp = false;
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
        this.signUp = (Button)findViewById(R.id.sign_up_okay);
        this.optionStudent = (RadioButton)findViewById(R.id.sign_up_student);
        this.optionProfessor = (RadioButton)findViewById(R.id.sign_up_professor);

        loading = (ProgressBar)findViewById(R.id.sign_up_loading);
        loading.setVisibility(View.GONE);

        currentlySigningUp = false;

        this.signUp.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (v != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }

                        String firstName = SignUp.this.firstNameBox.getText().toString();
                        String lastName = SignUp.this.lastNameBox.getText().toString();
                        String email = SignUp.this.emailBox.getText().toString().toLowerCase();
                        String age = SignUp.this.ageBox.getText().toString();

                        UserGender gender = UserGender.Male;
                        UserMode mode = UserMode.PassengerMode;
                        UserRole role = UserRole.Student;

                        boolean genderSelected =
                                SignUp.this.optionMale.isChecked()
                             || SignUp.this.optionFemale.isChecked()
                             || SignUp.this.optionOther.isChecked();

                        boolean roleSelected =
                                SignUp.this.optionStudent.isChecked()
                             || SignUp.this.optionProfessor.isChecked();

                        // Find the selected gender
                        if (SignUp.this.optionMale.isChecked()) gender = UserGender.Male;
                        else if (SignUp.this.optionFemale.isChecked()) gender = UserGender.Female;
                        else if (SignUp.this.optionOther.isChecked()) gender = UserGender.Other;

                        if (SignUp.this.optionStudent.isChecked()) role = UserRole.Student;
                        else if (SignUp.this.optionProfessor.isChecked()) role = UserRole.Professor;

                        // Find problems with the user input and report them.
                        if (firstName.length() == 0)
                            Toast.makeText(SignUp.this, "Invalid first name!", Toast.LENGTH_SHORT).show();
                        else if (lastName.length() == 0)
                            Toast.makeText(SignUp.this, "Invalid last name!", Toast.LENGTH_SHORT).show();
                        else if (email.length() == 0)
                            Toast.makeText(SignUp.this, "Invalid email!", Toast.LENGTH_SHORT).show();
                        else if (!email.endsWith("@aub.edu.lb") && !email.endsWith("@mail.aub.edu") && !email.endsWith("@aub.edu"))
                            Toast.makeText(SignUp.this, "Please use your university email!", Toast.LENGTH_SHORT).show();
                        else if (age.length() == 0)
                            Toast.makeText(SignUp.this, "Invalid age!", Toast.LENGTH_SHORT).show();
                        else if (!genderSelected)
                            Toast.makeText(SignUp.this, "Specify your gender!", Toast.LENGTH_SHORT).show();
                        else if (Integer.parseInt(age) < 14)
                            Toast.makeText(SignUp.this, "Age doesn't look right!", Toast.LENGTH_SHORT).show();
                        else if (!roleSelected)
                            Toast.makeText(SignUp.this, "Specify your role!", Toast.LENGTH_SHORT).show();
                        else {
                            if (!currentlySigningUp) {
                                currentlySigningUp = true;
                                new AsyncSignUp(firstName, lastName, email, Integer.parseInt(age), mode, gender, role).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                Toast.makeText(SignUp.this, "Hold your horses buddy.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
