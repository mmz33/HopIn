package aub.hopin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

public class SignUpConfirm extends AppCompatActivity {

    private EditText confirmCodeBox;
    private Button confirmButton;
    private TextView errorText;
    private String email;

    private class AsyncConfirm extends AsyncTask<Void, Void, Void> {
        private String code;
        private String errorMessage;

        protected void onPreExecute() {
            super.onPreExecute();
            code = confirmCodeBox.getText().toString();
            errorMessage = "";
        }

        protected Void doInBackground(Void... params) {
            String response = Server.confirmCode(email, code);
            if (response.equals("OK")) {
                Log.i("", "Successfully confirmed account.");
                ActiveUser.setActiveUserInfo(new UserInfo(email, true));
            } else {
                errorMessage = response;
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (errorMessage.length() > 0) {
                errorText.setText(errorMessage);
            } else {
                //startActivity(new Intent(SignUpConfirm.this, MapsActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_confirm);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        email = getIntent().getExtras().getString("email");

        confirmCodeBox = (EditText)findViewById(R.id.sign_up_confirm_box);
        confirmButton = (Button)findViewById(R.id.sign_confirm_next);
        errorText = (TextView)findViewById(R.id.sign_confirm_error_text);

        errorText.setText("");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AsyncConfirm().execute();
            }
        });
    }
}
