package aub.hopin;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class ReportProblem extends AppCompatActivity {

    private EditText reportProblemBox;
    private Button sendButton;
    private boolean currentlyReportingProblem;

    private class AsyncProblemReport extends AsyncTask<Void, Void, Void> {
        private String email;
        private String message;
        private boolean success;

        protected void onPreExecute() {
            super.onPreExecute();
            email = ActiveUser.getEmail();
            message = reportProblemBox.getText().toString();
        }

        protected Void doInBackground(Void... params) {
            try {
                success = Server.sendProblem(email, message).equals("OK");
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success) {
                Toast.makeText(ReportProblem.this, "Thank you!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ReportProblem.this, "Failed to send problem report.", Toast.LENGTH_SHORT).show();
                currentlyReportingProblem = false;
            }
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

        currentlyReportingProblem = false;

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (!currentlyReportingProblem) {
                    currentlyReportingProblem = true;
                    new AsyncProblemReport().execute();
                }
            }
        });
    }

}
