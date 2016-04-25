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
import android.widget.Toast;

import java.sql.Connection;

public class SignIn extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private TextView forgotPassword;
    private Button signIn;
    private ProgressBar loading;
    private boolean currentlySigningIn;

    private class AsyncSignIn extends AsyncTask<Void, Void, Void> {
        private String emailText;
        private String passwordText;
        private String customErrorMessage;
        private boolean success;

        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            emailText = email.getText().toString().toLowerCase();
            passwordText = password.getText().toString();
            customErrorMessage = "";
            success = false;
        }

        protected Void doInBackground(Void... params) {
            try {
                String response = Server.signIn(emailText, passwordText);
                if (response.startsWith("OK")) {
                    String ssid = response.substring(3);
                    ActiveUser.setSessionId(ssid);
                    ActiveUser.setInfo(UserInfoFactory.get(emailText, true));
                    success = true;
                } else {
                    customErrorMessage = response;
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loading.setVisibility(View.GONE);
            if (success) {
                SessionLoader.saveData(ActiveUser.getSessionId(), ActiveUser.getEmail());
                startActivity(new Intent(SignIn.this, MainMap.class));
                finish();
            } else {
                if (customErrorMessage.length() == 0) {
                    customErrorMessage = "Failed to connect to server!";
                }
                Toast.makeText(SignIn.this, customErrorMessage, Toast.LENGTH_SHORT).show();
                currentlySigningIn = false;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        email = (EditText) findViewById(R.id.sign_in_email);
        password = (EditText) findViewById(R.id.sign_in_password);
        forgotPassword = (TextView) findViewById(R.id.sign_in_forgot_password);
        signIn = (Button) findViewById(R.id.sign_in_button);

        loading = (ProgressBar) findViewById(R.id.sign_in_loading);
        loading.setVisibility(View.GONE);

        currentlySigningIn = false;

        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (!currentlySigningIn) {
                    currentlySigningIn = true;
                    new AsyncSignIn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
    }
}
