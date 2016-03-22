package aub.hopin;

import android.content.Intent;
import android.database.Cursor;
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

public class ProfileSettings extends AppCompatActivity {

    private ImageView profileImage;
    private Button uploadButton;
    private EditText status;
    private RadioButton passenger;
    private RadioButton driver;

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


        this.uploadButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        String path = selectedImage.getPath();
                        Server.sendProfilePicture(UserSession.getActiveSession(), path);
                    }
                });

        this.status.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        String s = ProfileSettings.this.status.getText().toString();
                        Server.sendStatus(UserSession.getActiveSession(), s);
                    }
                }
        );

        if(this.passenger.isChecked())
            Server.sendModeSwitch(UserSession.getActiveSession(), UserMode.PassengerMode);

        else if(this.driver.isChecked())
            Server.sendModeSwitch(UserSession.getActiveSession(), UserMode.DriverMode);
    }

    //click to select image
    public void loadProfileImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    String imgDecodableString;
    Uri selectedImage;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            // Move to first row
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            ImageView imgView = (ImageView) findViewById(R.id.profile_image_view);

            // Set the Image in ImageView after decoding the String
            imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
