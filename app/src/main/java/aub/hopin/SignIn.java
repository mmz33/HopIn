package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

public class SignIn extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private TextView forgotPassword;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.email = (EditText)findViewById(R.id.sign_in_email);
        this.password = (EditText)findViewById(R.id.sign_in_password);
        this.forgotPassword = (TextView)findViewById(R.id.sign_in_forgot_password);
        this.signIn = (Button)findViewById(R.id.sign_in_button);

        //TODO
        //Set up action listener to sign in && send data to server
        //TODO
        //Encrypt the password
    }
}
