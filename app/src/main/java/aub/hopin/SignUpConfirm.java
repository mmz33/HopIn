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
import android.widget.Toast;

public class SignUpConfirm extends AppCompatActivity {

    private EditText confirmCodeBox;
    private Button confirmButton;
    private String email;

    private ProgressBar loading;
    private boolean currentlyConfirming;

    private class AsyncConfirm extends AsyncTask<Void, Void, Void> {
        private String code;
        private boolean success;

        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            code = confirmCodeBox.getText().toString();
            success = false;
        }

        protected Void doInBackground(Void... params) {
            try {
                String response = Server.confirmCode(email, code);
                if (response.startsWith("OK")) {
                    String ssid = response.substring(3, response.indexOf('\n'));
                    String userInfoContent = response.substring(response.indexOf('\n') + 1);
                    ActiveUser.setSessionId(ssid);
                    ActiveUser.setInfo(UserInfoFactory.get(email, userInfoContent));
                    success = true;
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loading.setVisibility(View.GONE);
            if (!success) {
                Toast.makeText(SignUpConfirm.this, "Confirmation failed!", Toast.LENGTH_SHORT).show();
                currentlyConfirming = false;
            } else {
                SessionLoader.saveData(ActiveUser.getSessionId(), ActiveUser.getEmail());
                startActivity(new Intent(SignUpConfirm.this, MainMap.class));
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
        currentlyConfirming = false;

        confirmCodeBox = (EditText)findViewById(R.id.sign_up_confirm_box);
        confirmButton = (Button)findViewById(R.id.sign_confirm_next);

        loading = (ProgressBar)findViewById(R.id.sign_up_confirm_loading);
        loading.setVisibility(View.GONE);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!currentlyConfirming) {
                    currentlyConfirming = true;
                    new AsyncConfirm().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
    }
}
