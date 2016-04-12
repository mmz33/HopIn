package aub.hopin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
    private EditText statusBox;
    private RadioButton passengerButton;
    private RadioButton driverButton;
    private RadioGroup modeGroup;
    private RadioGroup stateGroup;
    private Uri selectedImage;

    private RadioButton passiveButton;
    private RadioButton offeringButton;
    private RadioButton wantingButton;

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
                user.setProfileImage(ResourceManager.getProfileImage(user.email)); // updates image from server.
                success = true;
                Log.i("error", "Successfully uploaded profile picture!");
            } else {
                Log.e("error", "Something went wrong with the profile picture update.");
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
            statusMessage = statusBox.getText().toString();
        }

        protected Void doInBackground(Void... params) {
            if (Server.sendStatus(email, statusMessage).equals("OK")) {
                Log.i("error", "Status update successfully sent to server.");
            } else {
                Log.e("error", "Something went wrong with the status update.");
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
            if (id == passengerButton.getId()) {
                mode = UserMode.PassengerMode;
            } else if (id == driverButton.getId()) {
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
                Log.i("error", "Successfully sent mode switch.");
            } else {
                Log.e("error", "Something went wrong with the mode switch.");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class AsyncStateChange extends AsyncTask<Void, Void, Void> {
        private String email;
        private UserState state;

        public AsyncStateChange(int id) {
            email = ActiveUser.getEmail();
            if (id == passiveButton.getId()) {
                state = UserState.Passive;
            } else if (id == offeringButton.getId()) {
                state = UserState.Offering;
            } else if (id == wantingButton.getId()) {
                state = UserState.Wanting;
            }
        }

        protected void onPreExecute() { super.onPreExecute(); }

        protected Void doInBackground(Void... params) {
            if (Server.sendStateSwitch(email, state).equals("OK")) {
                Log.i("error", "Successfully sent state switch.");
            } else {
                Log.e("error", "Something went wrong with the state switch.");
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
        statusBox = (EditText)findViewById(R.id.profile_status_message);

        modeGroup = (RadioGroup)findViewById(R.id.profile_switch_user_type_radio_group);

        passengerButton = (RadioButton)findViewById(R.id.profile_passenger_radio_button);
        driverButton = (RadioButton)findViewById(R.id.profile_driver_radio_button);

        stateGroup = (RadioGroup)findViewById(R.id.profile_driver_state_group);

        passiveButton = (RadioButton)findViewById(R.id.profile_state_group_passive);
        offeringButton = (RadioButton)findViewById(R.id.profile_state_group_offering);
        wantingButton = (RadioButton)findViewById(R.id.profile_state_group_want);

        UserInfo info = ActiveUser.getActiveUserInfo();

        // Set profile image.
        profileImage.setImageBitmap(info.profileImage);
        statusBox.setText(info.status);

        switch (info.mode) {
            case PassengerMode:
                passengerButton.setChecked(true);
                break;
            case DriverMode:
                driverButton.setChecked(true);
                break;
            default:
                break;
        }

        switch (info.state) {
            case Passive:
                passiveButton.setChecked(true);
                break;
            case Offering:
                offeringButton.setChecked(true);
                break;
            case Wanting:
                wantingButton.setChecked(true);
                break;
            default:
                break;
        }

        // Status message changes need to be sent to the server.
        statusBox.addTextChangedListener(new TextWatcher() {
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

        stateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int id) {
                new AsyncStateChange(id).execute();
            }
        });
    }

    //TODO
    // Fix profile pictures (galary)
    // Fix location manager updates.

    //click to select image
    public void loadProfileImage(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 0);
    }

    //This method return the path of the image.
    public static String getRealPathFromURI(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("error", "Activity result given.");
        if (data == null)
            Log.e("error", "Intent data is null.");
        else
            Log.i("error", "Intent data OK");

        Uri uri = data.getData();

        if (uri == null)
            Log.e("error", "Oops: uri is null");
        else
            Log.i("error", "Uri OK");

        new AsyncUploadProfilePicture(data.getData()).execute();

        //if(resultCode == Activity.RESULT_OK && data != null) {
           // String realPath = getRealPathFromURI(this, data.getData());
            //new AsyncUploadProfilePicture(realPath).execute();
        //}
    }
}
