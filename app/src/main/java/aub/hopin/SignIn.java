package aub.hopin;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Connection;

public class SignIn extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private TextView forgotPassword;
    private Button signIn;
    private TextView errorText;
    private ProgressBar loading;

    private class AsyncSignIn extends AsyncTask<Void, Void, Void> {
        private String emailText;
        private String passwordText;
        private String errorMessage;

        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            emailText = email.getText().toString().toLowerCase();
            passwordText = password.getText().toString();
            errorMessage = "";
            errorText.setText("");
        }

        protected Void doInBackground(Void... params) {
            try {
                String response = Server.signIn(emailText, passwordText);
                if (response.startsWith("OK")) {
                    String ssid = response.substring(3);
                    ActiveUser.setSessionId(ssid);
                    ActiveUser.setInfo(new UserInfo(emailText, true));
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
                SessionLoader.saveData(ActiveUser.getSessionId(), ActiveUser.getEmail());
                startActivity(new Intent(SignIn.this, SlideMenu.class));
                finish();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        email = (EditText)findViewById(R.id.sign_in_email);
        password = (EditText)findViewById(R.id.sign_in_password);
        forgotPassword = (TextView)findViewById(R.id.sign_in_forgot_password);
        signIn = (Button)findViewById(R.id.sign_in_button);
        errorText = (TextView)findViewById(R.id.sign_in_error_text);
        errorText.setText("");

        loading = (ProgressBar)findViewById(R.id.sign_in_loading);
        loading.setVisibility(View.GONE);

        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                new AsyncSignIn().execute();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {errorText.setText("");}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {errorText.setText("");}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
