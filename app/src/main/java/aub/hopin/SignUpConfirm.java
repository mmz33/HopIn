package aub.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

public class SignUpConfirm extends AppCompatActivity {

    private EditText confirmCodeBox;
    private Button confirmButton;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_confirm);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.confirmCodeBox = (EditText)findViewById(R.id.sign_up_confirm_box);
        this.confirmButton = (Button)findViewById(R.id.sign_confirm_next);
        this.errorText = (TextView)findViewById(R.id.sign_confirm_error_text);

        this.errorText.setText("");

        this.confirmButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread handler = new Thread(new Runnable() {
                        public void run() {
                            //ServerRequest request = Server.confirmCode(confirmCodeBox.getText().toString());
                            //while (request.status.get() == ServerRequestStatus.Pending.ordinal()) {
                            //    try { wait(32); } catch (Exception e) {}
                            //}
                            //UserSession session = (UserSession)request.response;
                            UserSession session = new UserSession(new UserInfo(), 90, 90);
                            if (session == null) {
                                errorText.setText("Invalid code.");
                            } else {
                                UserSession.setActiveSession(session);
                                startActivity(new Intent(SignUpConfirm.this, Map2Activity.class));
                                finish();
                            }
                        }
                    });
                    handler.start();
                }
            });
    }
}
