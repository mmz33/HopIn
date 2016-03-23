package aub.hopin;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private Button uploadButton;
    private EditText status;
    private RadioButton passenger;
    private RadioButton driver;
    private RadioGroup modeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.profileImage = (ImageView)findViewById(R.id.profile_image_view);
        this.uploadButton = (Button)findViewById(R.id.profile_upload_button);
        this.status = (EditText)findViewById(R.id.profile_status_message);
        this.passenger = (RadioButton)findViewById(R.id.profile_passenger_radio_button);
        this.driver = (RadioButton)findViewById(R.id.profile_driver_radio_button);
        this.modeGroup = (RadioGroup)findViewById(R.id.profile_switch_user_type_radio_group);

        UserSession session = UserSession.getActiveSession();
        UserInfo info = session.getUserInfo();

        // Load user profile image.
        // If it's not in the cache, load it from the server.
        //if (ResourceManager.isProfileImageLoaded(info.profilePicturePath)) {
        //    this.profileImage.setImageBitmap(ResourceManager.getProfileImage(info.profilePicturePath));
        //} else {
        //    Thread loader = new Thread(new Runnable() {
        //        @Override
        //        public void run() {
        //            UserSession session = UserSession.getActiveSession();
        //            UserInfo info = session.getUserInfo();
        //            Bitmap bm = ResourceManager.loadProfileImage(getApplicationContext(), info.profilePicturePath);
        //            profileImage.setImageBitmap(bm);
        //        }
        //    });
        //    loader.start();
        //}

        // Upload button sends an image to the server.
        this.uploadButton.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    Thread handler = new Thread(new Runnable() {
                        public void run() {
                            String path = selectedImage.getPath();
                            if (path == null) return;
                            if (path.length() == 0) return;
                            try {
                                //ServerRequest request = Server.sendProfilePicture(UserSession.getActiveSession(), path);
                                //while (request.status.get() == ServerRequestStatus.Pending.ordinal()) {
                                //    try {
                                //        wait(32);
                                //    } catch (Exception e) {
                                //        ErrorLogger.error("Something went wrong with the wait.");
                                //    }
                                //}
                                //profileImage.setImageBitmap(ResourceManager.loadProfileImage(getApplicationContext(), (String) request.response));
                            } catch (Exception e) {
                                ErrorLogger.error("Something went wrong with the profile picture uploading.");
                            }
                        }
                    });
                    handler.start();
                }
            });

        // Status message changes need to be sent to the server.
        this.status.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //Server.sendStatus(UserSession.getActiveSession(), s.toString());
                UserSession.getActiveSession().getUserInfo().status = s.toString();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // Mode switch handling.
        this.modeGroup.setOnCheckedChangeListener(
            new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int id) {
                    if (id == passenger.getId()) {
                        //Server.sendModeSwitch(UserSession.getActiveSession(), UserMode.PassengerMode);
                        UserSession.getActiveSession().getUserInfo().mode = UserMode.PassengerMode;
                    } else if (id == driver.getId()) {
                        //Server.sendModeSwitch(UserSession.getActiveSession(), UserMode.DriverMode);
                        UserSession.getActiveSession().getUserInfo().mode = UserMode.DriverMode;
                    }
                }
            });
    }

    //click to select image
    public void loadProfileImage(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    private String imgDecodableString;
    private Uri selectedImage;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            // Move to first row
            try {
                cursor.moveToFirst();
            } catch (NullPointerException npe) {
                ErrorLogger.error("Null pointer exception was produced in the cursor move to first call.");
            }
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            ImageView imgView = (ImageView) findViewById(R.id.profile_image_view);

            // Set the Image in ImageView after decoding the String
            imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
}
