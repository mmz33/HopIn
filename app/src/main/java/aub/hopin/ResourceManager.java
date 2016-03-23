package aub.hopin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.HashMap;
import android.content.Context;

public class ResourceManager {
    private static HashMap<String, Bitmap> pcache = null;
    private static HashMap<String, Bitmap> scache = null;

    private static void ensureCache() {
        if (pcache == null) {
            pcache = new HashMap<String, Bitmap>();
        }
        if (scache == null) {
            scache = new HashMap<String, Bitmap>();
        }
    }

    public static Bitmap getProfileImage(String path) {
        if (path == null || path.length() == 0) path = "default";
        ensureCache();
        if (pcache.containsKey(path)) {
            return pcache.get(path);
        }
        return null;
    }

    public static Bitmap getScheduleImage(String path) {
        if (path == null || path.length() == 0) path = "default";
        ensureCache();
        if (scache.containsKey(path)) {
            return scache.get(path);
        }
        return null;
    }

    public static Bitmap loadProfileImage(Context context, String path) {
        if (path == null || path.length() == 0) path = "default";
        ensureCache();
        if (path.equals("default")) {
            pcache.put(path, BitmapFactory.decodeResource(context.getResources(), R.drawable.missing_profile));
        } else {
            pcache.put(path, BitmapDownloader.downloadBitmap(path, 1, 65536));
        }
        return pcache.get(path);
    }

    public static Bitmap loadScheduleImage(Context context, String path) {
        if (path == null || path.length() == 0) path = "default";
        ensureCache();
        if (path.equals("default")) {
            scache.put(path, BitmapFactory.decodeResource(context.getResources(), R.drawable.unknown));
        } else {
            scache.put(path, BitmapDownloader.downloadBitmap(path, 1, 65536));
        }
        return scache.get(path);
    }

    public static boolean isProfileImageLoaded(String path) {
        if (path == null || path.length() == 0) path = "default";
        ensureCache();
        return pcache.containsKey(path);
    }

    public static boolean isScheduleImageLoaded(String path) {
        if (path == null || path.length() == 0) path = "default";
        ensureCache();
        return scache.containsKey(path);
    }

    public static void removeProfileImage(String path) {
        if (path == null || path.length() == 0) path = "default";
        if (isProfileImageLoaded(path)) {
            pcache.remove(path);
        }
    }

    public static void removeScheduleImage(String path) {
        if (path == null || path.length() == 0) path = "default";
        if (isScheduleImageLoaded(path)) {
            scache.remove(path);
        }
    }
}
