package aub.hopin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class ReportProblem extends AppCompatActivity {

    private EditText reportProblemBox;
    private Button sendButton;

    private class AsyncProblemReport extends AsyncTask<Void, Void, Void> {
        private String email;
        private String message;

        protected void onPreExecute() {
            super.onPreExecute();
            email = ActiveUser.getEmail();
            message = reportProblemBox.getText().toString();
        }

        protected Void doInBackground(Void... params) {
            if (Server.sendProblem(email, message).equals("OK")) {
                Log.i("", "Successfully sent problem message to server.");
            } else {
                Log.e("", "Failed to send problem message to server.");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        reportProblemBox = (EditText)findViewById(R.id.report_problem_textbox);
        sendButton = (Button)findViewById(R.id.report_problem_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AsyncProblemReport().execute();
            }
        });
    }

}
