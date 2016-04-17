package aub.hopin;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.Toast;

public class SignPage extends AppCompatActivity {
    private Button signIn;
    private Button signUp;

    private class AsyncAutologin extends AsyncTask<Void, Void, Void> {
        private String sessionId;
        private String email;
        private boolean success;

        public AsyncAutologin(String sessionId, String email) {
            this.sessionId = sessionId;
            this.email = email;
            this.success = false;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        public Void doInBackground(Void... params) {
            try {
                if (Server.checkSession(sessionId).equals("YES")) {
                    ActiveUser.setSessionId(sessionId);
                    ActiveUser.setInfo(new UserInfo(email, true));
                    success = true;
                } else {
                    success = false;
                }
            } catch (ConnectionFailureException e) {
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success) {
                Toast.makeText(getApplicationContext(), "Autologin Successful.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Autologin Failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_page);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This allows the volume buttons on the device
        // to directly affect the application itself.
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        LocalUserPreferences.init(this.getApplicationContext());
        ResourceManager.init(this.getApplicationContext());
        SessionLoader.init(this.getApplicationContext());

        // Attempt to load an active session.
        if (SessionLoader.existsSessionId()) {
            String ssid = SessionLoader.loadId();
            String email = SessionLoader.loadEmail();
            new AsyncAutologin(ssid, email).execute();
            Toast.makeText(getApplicationContext(), "Logging in automatically...", Toast.LENGTH_SHORT).show();
        }

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
