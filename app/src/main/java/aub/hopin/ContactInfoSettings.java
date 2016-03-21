package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class ContactInfoSettings extends AppCompatActivity {

    private EditText phoneNumberBox;
    private Button okayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.phoneNumberBox = (EditText)findViewById(R.id.contact_info_phone_number);
        this.okayButton = (Button)findViewById(R.id.contact_info_okay);

        this.phoneNumberBox.setText(UserSession.getActiveSession().getUserInfo().phoneNumber);

        this.okayButton.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    String number = ContactInfoSettings.this.phoneNumberBox.getText().toString();
                    Server.sendPhoneNumber(UserSession.getActiveSession(), number);
                }
            }
        );
    }
}
