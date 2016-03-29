package aub.hopin;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.RadioGroup;

public class ProfileSettings extends AppCompatActivity {

    private ImageView profileImage;
    private EditText status;
    private RadioButton passenger;
    private RadioButton driver;
    private RadioGroup modeGroup;
    private Uri selectedImage;

    private class AsyncUploadProfilePicture extends AsyncTask<Void, Void, Void> {
        private boolean success;
        private UserInfo user;
        private Uri uri;

        public AsyncUploadProfilePicture(Uri link) {
            uri = link;
            user = ActiveUser.getActiveUserInfo();
            success = false;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            if (Server.sendProfilePicture(user.email, selectedImage.getPath()).equals("OK")) {
                ResourceManager.setProfileImageDirty(user.email);
                user.profileImage = ResourceManager.getProfileImage(user.email); // updates image from server.
                success = true;
                Log.i("", "Successfully uploaded profile picture!");
            } else {
                Log.e("", "Something went wrong with the profile picture update.");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success)
                profileImage.setImageBitmap(user.profileImage);
        }
    }

    private class AsyncStatusUpdate extends AsyncTask<Void, Void, Void> {
        private String email;
        private String statusMessage;

        protected void onPreExecute() {
            super.onPreExecute();
            email = ActiveUser.getEmail();
            statusMessage = status.getText().toString();
        }

        protected Void doInBackground(Void... params) {
            if (Server.sendStatus(email, statusMessage).equals("OK")) {
                Log.i("", "Status update successfully sent to server.");
            } else {
                Log.e("", "Something went wrong with the status update.");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class AsyncModeChange extends AsyncTask<Void, Void, Void> {
        private String email;
        private UserMode mode;

        public AsyncModeChange(int id) {
            email = ActiveUser.getEmail();
            if (id == passenger.getId()) {
                mode = UserMode.PassengerMode;
            } else if (id == driver.getId()) {
                mode = UserMode.DriverMode;
            } else {
                mode = UserMode.Unspecified;
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            if (Server.sendModeSwitch(email, mode).equals("OK")) {
                Log.i("", "Successfully sent mode switch.");
            } else {
                Log.e("", "Something went wrong with the mode switch.");
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
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profileImage = (ImageView)findViewById(R.id.profile_image_view);
        passenger = (RadioButton)findViewById(R.id.profile_passenger_radio_button);
        modeGroup = (RadioGroup)findViewById(R.id.profile_switch_user_type_radio_group);
        status = (EditText)findViewById(R.id.profile_status_message);
        driver = (RadioButton)findViewById(R.id.profile_driver_radio_button);

        // Set profile image.
        profileImage.setImageBitmap(ActiveUser.getActiveUserInfo().profileImage);

        // Status message changes need to be sent to the server.
        status.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { new AsyncStatusUpdate().execute(); }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Mode switch handling.
        modeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int id) {
                new AsyncModeChange(id).execute();
            }
        });
    }

    //click to select image
    public void loadProfileImage(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new AsyncUploadProfilePicture(data.getData()).execute();
    }
}
