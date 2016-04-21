package aub.hopin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SignUpConfirm extends AppCompatActivity {

    private EditText confirmCodeBox;
    private Button confirmButton;
    private TextView errorText;
    private String email;

    private ProgressBar loading;

    private class AsyncConfirm extends AsyncTask<Void, Void, Void> {
        private String code;
        private String errorMessage;

        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            code = confirmCodeBox.getText().toString();
            errorMessage = "";
        }

        protected Void doInBackground(Void... params) {
            try {
                String response = Server.confirmCode(email, code);
                if (response.startsWith("OK")) {
                    String ssid = response.substring(3);
                    ActiveUser.setSessionId(ssid);
                    ActiveUser.setInfo(new UserInfo(email, true));
                } else {
                    errorMessage = response;
                }
            } catch (ConnectionFailureException e) {
                errorMessage = "Failed to connect to server. Try again.";
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loading.setVisibility(View.GONE);
            if (errorMessage.length() > 0) {
                errorText.setText(errorMessage);
            } else {
                startActivity(new Intent(SignUpConfirm.this, SlideMenu.class));
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

        loading = (ProgressBar)findViewById(R.id.sign_up_confirm_loading);
        loading.setVisibility(View.GONE);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AsyncConfirm().execute();
            }
        });

        confirmCodeBox.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { errorText.setText(""); }
            public void afterTextChanged(Editable s) {}
        });
    }
}
