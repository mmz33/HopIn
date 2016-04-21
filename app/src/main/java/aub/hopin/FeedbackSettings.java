package aub.hopin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Button;

public class FeedbackSettings extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button sendFeedbackButton;
    private Button reportProblemButton;

    private class AsyncSendRating extends AsyncTask<Void, Void, Void> {
        private float rating;
        private boolean fromUser;

        public AsyncSendRating(float rating, boolean fromUser) {
            this.rating = rating;
            this.fromUser = fromUser;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            if (fromUser) {
                try {
                    if (!Server.sendUserRating(ActiveUser.getEmail(), rating).equals("OK")) {
                        Log.e("", "There was a problem sending the user rating.");
                    }
                } catch (ConnectionFailureException e) {}
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ratingBar = (RatingBar)findViewById(R.id.feedback_rating_bar);
        sendFeedbackButton = (Button)findViewById(R.id.feedback_send_feedback);
        reportProblemButton = (Button)findViewById(R.id.feedback_report_problem);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar bar, float rating, boolean fromUser) {
                new AsyncSendRating(rating, fromUser).execute();
            }
        });

        sendFeedbackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(FeedbackSettings.this, SendFeedback.class));
            }
        });

        reportProblemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(FeedbackSettings.this, ReportProblem.class));
            }
        });
    }
}
