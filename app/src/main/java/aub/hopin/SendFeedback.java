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

public class SendFeedback extends AppCompatActivity {

    private EditText feedbackBox;
    private Button sendButton;

    private class AsyncSendFeedback extends AsyncTask<Void, Void, Void> {
        private String email;
        private String message;

        protected void onPreExecute() {
            super.onPreExecute();
            email = ActiveUser.getEmail();
            message = feedbackBox.getText().toString();
        }

        protected Void doInBackground(Void... params) {
            if (Server.sendFeedback(email, message).equals("OK")) {
                Log.i("", "Successfully sent feedback message to server.");
            } else {
                Log.e("", "Failed to send feedback message to server.");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        feedbackBox = (EditText)findViewById(R.id.send_feedback_textbox);
        sendButton = (Button)findViewById(R.id.send_feedback_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AsyncSendFeedback().execute();
            }
        });
    }
}
