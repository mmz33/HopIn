package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class ContactInformationSettings extends AppCompatActivity {

    private EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.phoneNumber = (EditText)findViewById(R.id.contact_information_phone_edit_text);

        this.phoneNumber.setOnClickListener(
              new View.OnClickListener() {
                  public void onClick(View v) {
                      String number = ContactInformationSettings.this.phoneNumber.getText().toString();
                      Server.sendPhoneNumber(UserSession.getActiveSession(), number);
                  }
              }
        );
    }

}
