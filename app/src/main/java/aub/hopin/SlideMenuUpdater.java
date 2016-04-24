package aub.hopin;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cmps on 4/24/16.
 */
public class SlideMenuUpdater {
    private static ImageView profileImageView;
    private static Timer updater;
    private static String currentHash;

    public static void start(ImageView im) {
        profileImageView = im;

        updater = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                UserInfo info = ActiveUser.getInfo();
                if (info == null) return;
                if (!info.isValid()) return;

                String hash = info.getProfileImageHash();
                if (!currentHash.equals(hash)) {
                    currentHash = hash;
                    profileImageView.setImageBitmap(info.profileImage);
                }
            }
        };
        updater.scheduleAtFixedRate(task, 1000, 1000);
    }

    public static void stop() {
        updater.cancel();
    }
}
