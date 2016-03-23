package aub.hopin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Button;
import android.widget.EditText;

public class SendFeedback extends AppCompatActivity {

    private EditText feedbackBox;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.feedbackBox = (EditText)findViewById(R.id.send_feedback_textbox);
        this.sendButton = (Button)findViewById(R.id.send_feedback_send);

        this.sendButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Server.sendFeedback(UserSession.getActiveSession(), feedbackBox.getText().toString());
                    finish();
                }
            });
    }
}
