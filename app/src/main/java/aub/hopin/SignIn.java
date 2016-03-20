package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

public class SignIn extends AppCompatActivity {
    EditText email;
    EditText password;
    TextView forgotPassword;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        email = (EditText)findViewById(R.id.sign_in_email);
        password = (EditText)findViewById(R.id.sign_in_password);
        forgotPassword = (TextView)findViewById(R.id.sign_in_forgot_password);
        signIn = (Button)findViewById(R.id.sign_in_button);

        //TODO
        //Set up action listener to sign in && send data to server
        //TODO
        //Encrypt the password
    }
}
