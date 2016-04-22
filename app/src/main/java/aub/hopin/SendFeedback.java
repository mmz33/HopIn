package aub.hopin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendFeedback extends AppCompatActivity {

    private EditText feedbackBox;
    private TextView errorText;
    private Button sendButton;

    private class AsyncSendFeedback extends AsyncTask<Void, Void, Void> {
        private String email;
        private String message;
        private String errorMessage;
        private boolean success;

        protected void onPreExecute() {
            super.onPreExecute();
            email = ActiveUser.getEmail();
            message = feedbackBox.getText().toString();
            success = false;
        }

        protected Void doInBackground(Void... params) {
            try {
                if (Server.sendFeedback(email, message).equals("OK")) {
                    success = true;
                }
            } catch (ConnectionFailureException e) {
                errorMessage = "Failed to connect to server. Try again.";
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success) {
                Toast.makeText(getApplicationContext(), "Successfully sent feedback!", Toast.LENGTH_SHORT);
                finish();
            } else {
                errorText.setText(errorMessage);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        feedbackBox = (EditText)findViewById(R.id.send_feedback_textbox);
        sendButton = (Button)findViewById(R.id.send_feedback_send);
        errorText = (TextView)findViewById(R.id.send_feedback_error_text);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AsyncSendFeedback().execute();
            }
        });
    }
}
