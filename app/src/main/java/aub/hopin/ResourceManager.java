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
        if (cache == null) {
            cache = new HashMap<>();
        }
    }

    private static String resourceName(String email) {
        return "profile_" + email;
    }

    public static Bitmap getProfileImage(String email) {
        ensureCache();
        try {
            if (email == null || email.length() == 0) {
                return defaultProfileImage;
            } else {
                String name = resourceName(email);
                if (cache.containsKey(name)) {
                    return cache.get(name);
                } else {
                    Bitmap bmp = Server.downloadProfileImage(email);
                    cache.put(name, bmp == null? defaultProfileImage : bmp);
                    return cache.get(name);
                }
            }
        } catch (ConnectionFailureException e) {
            Log.e("error", "Failed to get profile image from resource manager.");
            return null;
        }
    }

    public static void setProfileImageDirty(String email) {
        ensureCache();
        if (email == null || email.length() == 0) {
            return;
        } else {
            String name = resourceName(email);
            if (cache.containsKey(name)) {
                cache.remove(name);
            }
        }
    }
}
