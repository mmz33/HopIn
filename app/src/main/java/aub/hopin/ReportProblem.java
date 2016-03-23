package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class ReportProblem extends AppCompatActivity {

    private EditText reportProblemBox;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.reportProblemBox = (EditText)findViewById(R.id.report_problem_textbox);
        this.sendButton = (Button)findViewById(R.id.report_problem_send);

        this.sendButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Server.sendProblem(UserSession.getActiveSession(), reportProblemBox.getText().toString());
                    finish();
                }
            }
        );
    }

}
