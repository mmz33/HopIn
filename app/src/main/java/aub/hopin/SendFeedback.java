package aub.hopin;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendFeedback extends AppCompatActivity {

    private EditText feedbackBox;
    private Button sendButton;
    private boolean currentlySendingFeedback;

    private class AsyncSendFeedback extends AsyncTask<Void, Void, Void> {
        private String email;
        private String message;
        private boolean success;

        protected void onPreExecute() {
            super.onPreExecute();
            email = ActiveUser.getEmail();
            message = feedbackBox.getText().toString();
            success = false;
        }

        protected Void doInBackground(Void... params) {
            try {
                success = Server.sendFeedback(email, message).equals("OK");
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success) {
                Toast.makeText(SendFeedback.this, "Thank you!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(SendFeedback.this, "Failed to send feedback.", Toast.LENGTH_SHORT).show();
                currentlySendingFeedback = false;
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

        currentlySendingFeedback = false;

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (!currentlySendingFeedback) {
                    currentlySendingFeedback = true;
                    new AsyncSendFeedback().execute();
                }
            }
        });
    }
}
