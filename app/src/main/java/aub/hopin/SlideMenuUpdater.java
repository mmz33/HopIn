package aub.hopin;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SlideMenuUpdater {
    private static ImageView profileImageView;
    private static Timer updater;

    public static void start(ImageView im) {
        if (im == null) throw new IllegalArgumentException();

        profileImageView = im;
        updater = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Log.e("slide", "ran the handler");

                UserInfo info = ActiveUser.getInfo();
                if (info == null) return;

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        UserInfo info = ActiveUser.getInfo();
                        if (info != null) {
                            Bitmap bmp = info.getProfileImage();
                            if (bmp != null) {
                                profileImageView.setImageBitmap(bmp);
                            } else {
                                profileImageView.setImageBitmap(ResourceManager.getDefaultProfileImage());
                            }
                        }
                    }
                });
            }
        };
        updater.scheduleAtFixedRate(task, 1000, 3000);
    }

    public static void stop() {
        updater.cancel();
    }
}
