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

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class  ProfileSettings extends AppCompatActivity {
    private static final int PICK_FROM_GALLERY = 1;
    private static final int CROP_IMAGE = 2;

    private ImageView profileImage;
    private EditText statusBox;

    private ProgressBar loading;

    private TextView profilePhone;
    private TextView profileEmail;
    private TextView profileVehicle;

    private TextView profileRole;

    private UserInfo profileInfo;
    private View.OnClickListener clickListener;

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
                if (Server.sendProfilePicture(profileInfo.email, bitmap).equals("OK")) {
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
                profileImage.setImageBitmap(profileInfo.getProfileImage());
            } else {
                Toast.makeText(ProfileSettings.this, "Failed to upload image.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(ProfileSettings.this, "Failed to change status.", Toast.LENGTH_SHORT).show();
            }
            statusBox.setText(profileInfo.status);
        }
    }

    // Important:
    // ----------
    //
    // Any change to the user interface should be done inside this method only.
    // This is because this method will be called any time the information
    // needed to display the user interface is available.
    //
    // Changing the user interface to content related to profileInfo outside
    // of this method may result in errors.
    //
    // It's okay to change the user interface to things unrelated to
    // the profileInfo variable outside of this method.
    //
    public void displayContent() {
        TextView title = (TextView)findViewById(R.id.profile_title);
        if (title != null) title.setText(profileInfo.firstName + " " + profileInfo.lastName);

        profileImage.setImageBitmap(profileInfo.getProfileImage());
        statusBox.setText(profileInfo.status);

        profilePhone.setText(profileInfo.showingPhone ? profileInfo.phoneNumber : "Hidden");
        profileEmail.setText(profileInfo.email);

        switch (profileInfo.role) {
            case Student:   profileRole.setText("Student");   break;
            case Professor: profileRole.setText("Professor"); break;
            default:        throw new IllegalArgumentException();
        }

        if (!profileInfo.vehicle.make.equals("")) {
            String vehicleString = "";
            vehicleString += profileInfo.vehicle.color;
            vehicleString += " ";
            vehicleString += profileInfo.vehicle.make;
            vehicleString += " for ";
            vehicleString += profileInfo.vehicle.capacity;
            vehicleString += " people.";
            profileVehicle.setText(vehicleString);
        } else {
            profileVehicle.setText("No vehicle.");
        }

        if (clickListener == null) {
            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettings.this);
                            builder.setTitle("Edit Your Status");

                            final EditText input = new EditText(ProfileSettings.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            input.setText(profileInfo.status);
                            builder.setView(input);

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AsyncStatusUpdate(input.getText().toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profileImage = (ImageView)findViewById(R.id.profile_image_view);
        statusBox = (EditText)findViewById(R.id.profile_status_message);
        loading = (ProgressBar)findViewById(R.id.profile_loading);

        profilePhone = (TextView)findViewById(R.id.profile_user_phone);
        profileEmail = (TextView)findViewById(R.id.profile_user_email);
        profileVehicle = (TextView)findViewById(R.id.profile_vehicle_type);

        profileRole = (TextView)findViewById(R.id.profile_role);

        clickListener = null;
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
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            cropIntent.putExtra("return-data", true);

            startActivityForResult(cropIntent, CROP_IMAGE);
        } catch (ActivityNotFoundException e) {}
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_GALLERY:
                Uri uri = data.getData();
                performCrop(uri);
                break;
            case CROP_IMAGE:
                Bundle extras = data.getExtras();
                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    new AsyncUploadProfilePictureBitmap(photo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
            default:
                break;
        }
    }
}
