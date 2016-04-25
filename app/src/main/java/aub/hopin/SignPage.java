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
                    ActiveUser.setInfo(UserInfoFactory.get(email, true));
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
                Toast.makeText(SignPage.this, "Autologin Successful.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignPage.this, MainMap.class));
            } else {
                Toast.makeText(SignPage.this, "Autologin Failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_page);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GlobalContext.init(getApplicationContext());

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
            new AsyncAutologin(ssid, email).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Toast.makeText(SignPage.this, "Logging in automatically...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SignPage.this, "No session found...", Toast.LENGTH_SHORT).show();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_page, menu);
        return true;
    }
}
