package aub.hopin;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Connection;

public class ScheduleSettings extends AppCompatActivity {

    private ImageView scheduleImage;
    private Uri selectedImage;

    private class AsyncUploadScheduleImage extends AsyncTask<Void, Void, Void> {
        private boolean success;
        private UserInfo user;
        private Uri uri;

        public AsyncUploadScheduleImage(Uri link) {
            uri = link;
            user = ActiveUser.getInfo();
            success = false;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                if (Server.sendSchedule(user.email, selectedImage.getPath()).equals("OK")) {
                    ResourceManager.setScheduleImageDirty(user.email);
                    user.scheduleImage = ResourceManager.getScheduleImage(user.email); // updates image from server.
                    success = true;
                    Log.i("", "Successfully uploaded schedule picture!");
                } else {
                    Log.e("", "Something went wrong with the schedule picture update.");
                }
            } catch (ConnectionFailureException e) {
                // TODO
                // display message
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success)
                scheduleImage.setImageBitmap(user.profileImage);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.scheduleImage = (ImageView)findViewById(R.id.schedule_image_view);
        this.scheduleImage.setImageBitmap(ActiveUser.getInfo().scheduleImage);
    }

    public void loadScheduleImage(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new AsyncUploadScheduleImage(data.getData()).execute();
    }
}
