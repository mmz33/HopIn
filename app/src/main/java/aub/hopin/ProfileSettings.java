package aub.hopin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.RadioGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ProfileSettings extends AppCompatActivity {
    private static final int PICK_FROM_GALLERY = 1;
    private static final int CROP_IMAGE = 2;

    private ImageView profileImage;
    private EditText statusBox;
    private RadioButton passengerButton;
    private RadioButton driverButton;
    private RadioGroup modeGroup;
    private RadioGroup stateGroup;

    private RadioButton passiveButton;
    private RadioButton offeringButton;
    private RadioButton wantingButton;

    private ProgressBar loading;

    private UserInfo profileInfo;
    private View.OnClickListener clickListener;
    private RadioGroup.OnCheckedChangeListener listener0;
    private RadioGroup.OnCheckedChangeListener listener1;

    public class AsyncUploadProfilePictureBitmap extends AsyncTask<Void, Void, Void> {
        private boolean success;
        private Bitmap bitmap;

        public AsyncUploadProfilePictureBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            this.success = false;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if(Server.sendProfilePicture(profileInfo.email, bitmap).equals("OK")) {
                    ResourceManager.setProfileImageDirty(profileInfo.email);
                    profileInfo.setProfileImage(ResourceManager.getProfileImage(profileInfo.email));
                    success = true;
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPreExecute();
            loading.setVisibility(View.GONE);
            if (success) {
                profileImage.setImageBitmap(profileInfo.profileImage);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to upload image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncStatusUpdate extends AsyncTask<Void, Void, Void> {
        private String statusMessage;
        private boolean success;

        public AsyncStatusUpdate(String message) {
            this.statusMessage = message;
            this.success = false;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                if (Server.sendStatus(profileInfo.email, statusMessage).equals("OK")) {
                    profileInfo.status = statusMessage;
                    success = true;
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                Toast.makeText(getApplicationContext(), "Failed to change status.", Toast.LENGTH_SHORT).show();
            }
            statusBox.setText(profileInfo.status);
        }
    }

    private class AsyncModeChange extends AsyncTask<Void, Void, Void> {
        private UserMode mode;
        private boolean success;

        public AsyncModeChange(int id) {
            if (id == passengerButton.getId()) {
                mode = UserMode.PassengerMode;
            } else if (id == driverButton.getId()) {
                mode = UserMode.DriverMode;
            } else {
                mode = UserMode.Unspecified;
            }
            success = false;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                if (Server.sendModeSwitch(profileInfo.email, mode).equals("OK")) {
                    success = true;
                    profileInfo.mode = mode;
                    ModeSwitchEvent.fire(profileInfo.email);
                } else {
                    Log.e("error", "Something went wrong with the mode switch.");
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                Toast.makeText(getApplicationContext(), "Failed to switch mode.", Toast.LENGTH_SHORT).show();
                passengerButton.setChecked(false);
                driverButton.setChecked(false);
                switch (profileInfo.mode) {
                    case PassengerMode:
                        passengerButton.setChecked(true);
                        break;
                    case DriverMode:
                        driverButton.setChecked(true);
                        break;
                    case Unspecified:
                        break;
                }
            }
        }
    }

    private class AsyncStateChange extends AsyncTask<Void, Void, Void> {
        private UserState state;
        private boolean success;

        public AsyncStateChange(int id) {
            if (id == passiveButton.getId()) {
                state = UserState.Passive;
            } else if (id == offeringButton.getId()) {
                state = UserState.Offering;
            } else if (id == wantingButton.getId()) {
                state = UserState.Wanting;
            }
            success = false;
        }

        protected void onPreExecute() { super.onPreExecute(); }

        protected Void doInBackground(Void... params) {
            try {
                if (Server.sendStateSwitch(profileInfo.email, state).equals("OK")) {
                    profileInfo.state = state;
                    success = true;
                } else {
                    Log.e("error", "Something went wrong with the state switch.");
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                Toast.makeText(getApplicationContext(), "Failed to switch state.", Toast.LENGTH_SHORT).show();
                passiveButton.setChecked(false);
                offeringButton.setChecked(false);
                wantingButton.setChecked(false);
                switch (profileInfo.state) {
                    case Passive:
                        passiveButton.setChecked(true);
                        break;
                    case Offering:
                        offeringButton.setChecked(true);
                        break;
                    case Wanting:
                        wantingButton.setChecked(true);
                        break;
                }
            }
        }
    }

    public void displayContent() {
        TextView title = (TextView)findViewById(R.id.profile_title);
        if (title != null) title.setText(profileInfo.firstName + "'s Profile");

        profileImage.setImageBitmap(profileInfo.profileImage);
        statusBox.setText(profileInfo.status);

        switch (profileInfo.mode) {
            case PassengerMode:
                passengerButton.setChecked(true);
                break;
            case DriverMode:
                driverButton.setChecked(true);
                break;
            default:
                break;
        }

        switch (profileInfo.state) {
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

        if (clickListener == null) {
            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettings.this);
                            builder.setTitle("Status");
                            builder.setMessage("Enter status");

                            final EditText input = new EditText(ProfileSettings.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            builder.setView(input);

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AsyncStatusUpdate(input.getText().toString()).execute();
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    });
                }
            };
            statusBox.setOnClickListener(clickListener);
        }

        if (listener0 == null) {
            listener0 = new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int id) {
                    new AsyncModeChange(id).execute();
                }
            };
            modeGroup.setOnCheckedChangeListener(listener0);
        }

        if (listener1 == null) {
            listener1 = new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int id) {
                    new AsyncStateChange(id).execute();
                }
            };
            stateGroup.setOnCheckedChangeListener(listener1);
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
        loading = (ProgressBar)findViewById(R.id.profile_loading);

        clickListener = null;
        listener0 = null;
        listener1 = null;

        if (loading != null) loading.setVisibility(View.GONE);

        String email = getIntent().getExtras().getString("email");
        if (email == null) email = ActiveUser.getEmail();

        if (email.equals(ActiveUser.getEmail())) {
            profileInfo = ActiveUser.getInfo();
            displayContent();
        } else {
            profileInfo = new UserInfo(email, false,
                    new Runnable() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    displayContent();
                                }
                            });
                        }
                    });
        }
    }

    //click to select image
    public void loadProfileImage(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        try {
            startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
        } catch (ActivityNotFoundException e) {}
    }

    //This method return the path of the image.
    public static String getRealPathFromURI(Context context, Uri uri){
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void performCrop(Uri uri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(uri, "image/*");

            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", true);
            cropIntent.putExtra("aspectY", true);
            cropIntent.putExtra("outputX", 100);
            cropIntent.putExtra("outputY", 100);
            cropIntent.putExtra("return-data", true);

            startActivityForResult(cropIntent, CROP_IMAGE);
        } catch (ActivityNotFoundException e) {}
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_GALLERY:
                Uri uri = data.getData();
                performCrop(uri);
                break;
            case CROP_IMAGE:
                Bundle extras = data.getExtras();
                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    new AsyncUploadProfilePictureBitmap(photo).execute();
                }
                break;
            default:
                break;
        }
    }
}
