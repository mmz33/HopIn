package aub.hopin;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class LocalUserPreferences {
    private static SharedPreferences prefs = null;
    private static ArrayList<Runnable> callbacks = new ArrayList<>();

    private static final boolean TILT_MAP_DEFAULT = false;
    private static final boolean ZOOM_MAP_DEFAULT = true;
    private static final boolean ROTATE_MAP_DEFAULT = false;

    public static void init(Context ctx) {
        if (prefs == null) {
            prefs = ctx.getSharedPreferences("prefs", 0);
        }
    }

    public static void setTiltMap(boolean b) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("tilt", b);
        edit.commit();

        for (Runnable f : callbacks) {
            f.run();
        }
    }

    public static void setZoomMap(boolean b) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("zoom", b);
        edit.commit();

        for (Runnable f : callbacks) {
            f.run();
        }
    }

    public static void setRotateMap(boolean b) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("rotate", b);
        edit.commit();

        for (Runnable f : callbacks) {
            f.run();
        }
    }

    public static boolean getTiltMap() {
        return prefs.getBoolean("tilt", TILT_MAP_DEFAULT);
    }
    public static boolean getZoomMap() {
        return prefs.getBoolean("zoom", ZOOM_MAP_DEFAULT);
    }
    public static boolean getRotateMap() {
        return prefs.getBoolean("rotate", ROTATE_MAP_DEFAULT);
    }

    public static void registerOnChangeListener(Runnable runnable) {
        callbacks.add(runnable);
    }
}
