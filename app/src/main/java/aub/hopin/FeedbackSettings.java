package aub.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Button;

public class FeedbackSettings extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button sendFeedbackButton;
    private Button reportProblemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ratingBar = (RatingBar)findViewById(R.id.feedback_rating_bar);
        sendFeedbackButton = (Button)findViewById(R.id.feedback_send_feedback);
        reportProblemButton = (Button)findViewById(R.id.feedback_report_problem);

        ratingBar.setOnRatingBarChangeListener(
            new RatingBar.OnRatingBarChangeListener() {
                public void onRatingChanged(RatingBar bar, float rating, boolean fromUser) {
                    if (fromUser) {
                        //Server.sendUserRating(UserSession.getActiveSession(), rating);
                    }
                }
            });

        sendFeedbackButton.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(FeedbackSettings.this, SendFeedback.class));
                }
            });

        reportProblemButton.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(FeedbackSettings.this, ReportProblem.class));
                }
            });
    }
}
