package aub.hopin;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ScheduleSettings extends AppCompatActivity {

    private ImageView scheduleImage;
    private Button uploadSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.scheduleImage = (ImageView)findViewById(R.id.schedule_image_view);
        this.uploadSchedule = (Button)findViewById(R.id.schedule_upload_button);

        this.uploadSchedule.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    Thread handler = new Thread(new Runnable() {
                        public void run() {
                            String path = selectedImage.getPath();
                            if (path == null) return;
                            if (path.length() == 0) return;
                            try {
                                //ServerRequest request = Server.sendSchedule(UserSession.getActiveSession(), path);
                                //while (request.status.get() == ServerRequestStatus.Pending.ordinal()) {
                                //    try {
                                //        wait(32);
                                //    } catch (Exception e) {
                                //        ErrorLogger.error("Something went wrong with the wait.");
                                //    }
                                //}
                                //scheduleImage.setImageBitmap(ResourceManager.loadScheduleImage(getApplicationContext(), (String) request.response));
                            } catch (Exception e) {
                                ErrorLogger.error("Something went wrong with the schedule picture uploading.");
                            }
                        }
                    });
                    handler.start();
                }
            });


    }

    //click to select image
    public void loadScheduleImage(View v) {
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
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            ImageView imgView = (ImageView)findViewById(R.id.schedule_image_view);

            // Set the Image in ImageView after decoding the String
            imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
}
