package aub.hopin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

public class SignPage extends AppCompatActivity {
    private Button signIn;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_page);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create buttons.
        this.signIn = (Button)findViewById(R.id.sign_page_sign_in);
        this.signUp = (Button)findViewById(R.id.sign_page_sign_up);

        // Setup callbacks.
        this.signIn.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(SignPage.this, SignIn.class));
                }
            }
        );

        this.signUp.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(SignPage.this, SignUp.class));
                }
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
