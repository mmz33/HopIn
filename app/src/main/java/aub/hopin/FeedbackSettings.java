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
import android.widget.Toast;

public class FeedbackSettings extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button sendFeedbackButton;
    private Button reportProblemButton;

    private boolean ratingSendEnabled;

    private class AsyncSendRating extends AsyncTask<Void, Void, Void> {
        private float rating;
        private boolean fromUser;
        private boolean success;

        public AsyncSendRating(float rating, boolean fromUser) {
            this.rating = rating;
            this.fromUser = fromUser;
            this.success = false;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            if (fromUser) {
                try {
                    if (Server.sendUserRating(ActiveUser.getEmail(), rating).equals("OK")) {
                        success = true;
                    }
                } catch (ConnectionFailureException e) {}
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                Toast.makeText(getApplicationContext(), "Failed to send rating.", Toast.LENGTH_SHORT).show();
                ratingSendEnabled = false;
                ratingBar.setRating(0.0f);
                ratingSendEnabled = true;
            } else {
                Toast.makeText(getApplicationContext(), "Thank you!", Toast.LENGTH_SHORT).show();
            }
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

        ratingSendEnabled = true;

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar bar, float rating, boolean fromUser) {
                if (ratingSendEnabled) new AsyncSendRating(rating, fromUser).execute();
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
