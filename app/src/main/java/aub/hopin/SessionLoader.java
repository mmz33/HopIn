package aub.hopin;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionLoader {
    private static SharedPreferences prefs = null;

    public static void init(Context ctx) {
        if (prefs == null) {
            prefs = ctx.getSharedPreferences("sessioninfo", 0);
        }
    }

    public static boolean existsSessionId() {
        return prefs.getBoolean("stored", false);
    }

    public static String loadId() {
        if (existsSessionId())
            return prefs.getString("ssid", null);
        return null;
    }

    public static String loadEmail() {
        if (existsSessionId())
            return prefs.getString("email", null);
        return null;
    }

    public static void saveData(String ssid, String email) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("ssid", ssid);
        edit.putString("email", email);
        edit.putBoolean("stored", true);
        edit.commit();
    }

    public static void clean() {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("ssid", "");
        edit.putString("email", "");
        edit.putBoolean("stored", false);
        edit.commit();
    }
}
