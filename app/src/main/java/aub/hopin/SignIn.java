package aub.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

public class SignIn extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private TextView forgotPassword;
    private Button signIn;
    private TextView errorText;

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
        this.errorText = (TextView)findViewById(R.id.sign_in_error_text);

        this.errorText.setText("");

        this.signIn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread handler = new Thread(new Runnable() {
                        public void run() {
                            //ServerRequest request = Server.signIn(email.getText().toString(), password.getText().toString());
                            //while (request.status.get() == ServerRequestStatus.Pending.ordinal()) {
                            //    try { wait(32); } catch (Exception e) {}
                            //}
                            //UserSession session = (UserSession)(request.response);
                            UserSession session = new UserSession(new UserInfo(), 90, 91);
                            if (session == null) {
                                errorText.setText("Invalid credentials!");
                            } else {
                                UserSession.setActiveSession(session);
                                startActivity(new Intent(SignIn.this, SlideMenu.class));
                                finish();
                            }
                        }
                    });
                    handler.start();
                }
            });
    }
}
