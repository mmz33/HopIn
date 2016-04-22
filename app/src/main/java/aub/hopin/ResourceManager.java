package aub.hopin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.HttpURLConnection;
import java.util.HashMap;
import android.content.Context;
import android.util.Log;

public class ResourceManager {
    private static HashMap<String, Bitmap> cache = null;
    private static Bitmap defaultProfileImage = null;

    public static void init(Context context) {
        defaultProfileImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.missing_profile);
    }

    private static void ensureCache() {
        if (cache == null) cache = new HashMap<String, Bitmap>();
    }

    public static Bitmap getProfileImage(String email) {
        ensureCache();
        try {
            if (email == null || email.length() == 0) {
                return defaultProfileImage;
            } else {
                String name = "profile_" + email;
                if (cache.containsKey(name)) {
                    return cache.get(name);
                } else {
                    Bitmap bmp = Server.downloadProfileImage(email);
                    cache.put(name, bmp == null? defaultProfileImage : bmp);
                    return cache.get(name);
                }
            }
        } catch (Throwable t) {
            Log.e("error", "Failed to get profile image from resource manager.");
            return null;
        }
    }

    public static void setProfileImageDirty(String email) {
        ensureCache();
        if (email == null || email.length() == 0) {
            return;
        } else {
            String name = "profile_" + email;
            if (cache.containsKey(name)) {
                cache.remove(name);
            }
        }
    }
}
