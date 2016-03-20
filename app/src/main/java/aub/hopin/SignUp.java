package aub.hopin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.TextView;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SignUp extends AppCompatActivity {

    EditText firstNameBox;
    EditText lastNameBox;
    EditText emailBox;
    EditText ageBox;

    RadioButton optionMale;
    RadioButton optionFemale;
    RadioButton optionOther;

    RadioButton optionDriver;
    RadioButton optionPassenger;

    Button signUp;
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firstNameBox = (EditText)findViewById(R.id.sign_up_first_name);
        lastNameBox = (EditText)findViewById(R.id.sign_up_last_name);
        emailBox = (EditText)findViewById(R.id.sign_up_email);
        ageBox = (EditText)findViewById(R.id.sign_up_age);

        optionMale = (RadioButton)findViewById(R.id.sign_up_male);
        optionFemale = (RadioButton)findViewById(R.id.sign_up_female);
        optionOther = (RadioButton)findViewById(R.id.sign_up_other);

        optionDriver = (RadioButton)findViewById(R.id.sign_up_driver);
        optionPassenger = (RadioButton)findViewById(R.id.sign_up_passenger);

        signUp = (Button)findViewById(R.id.sign_up_okay);

        signUp.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        String firstName = firstNameBox.getText().toString();
                        String lastName  = lastNameBox.getText().toString();
                        String email     = emailBox.getText().toString();
                        String age       = ageBox.getText().toString();

                        // Find the selected gender.
                        int gender = -1;
                        if (optionMale.isChecked())
                            gender = 0;
                        else if (optionFemale.isChecked())
                            gender = 1;
                        else if (optionOther.isChecked())
                            gender = 2;

                        // Find the selected mode.
                        int mode = -1;
                        if (optionDriver.isChecked())
                            mode = 0;
                        else if (optionPassenger.isChecked())
                            mode = 1;

                        if (firstName.length() == 0)
                            errorText.setText("Please input first name.");
                        else if (lastName.length() == 0)
                            errorText.setText("Please input last name.");
                        else if (email.length() == 0)
                            errorText.setText("Please input email.");
                        else if (!email.endsWith("@aub.edu.lb") && !email.endsWith("@mail.aub.edu") && !email.endsWith("@aub.edu"))
                            errorText.setText("Please use your university email.");
                        else if (age.length() == 0)
                            errorText.setText("Please input age.");
                        else if (gender == -1)
                            errorText.setText("Please specify gender.");
                        else if (mode == -1)
                            errorText.setText("Please specify mode.");
                        else if (Integer.parseInt(age)  < 16 )
                            errorText.setText("You must be 16 or older.");
                        else {
                            try {
                                Socket socket = new Socket("192.168.192.161", 2525);
                                OutputStream oStream = socket.getOutputStream();
                                PrintWriter streamWriter = new PrintWriter(oStream);
                                streamWriter.println("*25*NEW_USER");
                                streamWriter.println(firstName);
                                streamWriter.println(lastName);
                                streamWriter.println(email);
                                streamWriter.println(age);
                                streamWriter.println(gender);
                                streamWriter.println(mode);


                            } catch(Exception e) {
                            }
                        }
                    }
                }
        );
    }
}
