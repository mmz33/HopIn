package aub.hopin;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalUserPreferences {
    private static SharedPreferences prefs = null;

    public static void init(Context ctx) {
        if (prefs == null) {
            prefs = ctx.getSharedPreferences("prefs", 0);
        }
    }

    public static MeasurementUnits getUnits() {
        MeasurementUnitsSetting setting = getUnitsSetting();

        switch (setting) {
            case Metric:
                return MeasurementUnits.Metric;
            case Imperial:
                return MeasurementUnits.Imperial;
            case Automatic:
                String locale = GlobalContext.get().getResources().getConfiguration().locale.getCountry();
                if (locale.equals("US")) {
                    return MeasurementUnits.Imperial;
                } else {
                    return MeasurementUnits.Metric;
                }
            default:
                return MeasurementUnits.Metric;
        }
    }

    public static MeasurementUnitsSetting getUnitsSetting() {
        int ordinal = prefs.getInt("unitsetting", MeasurementUnitsSetting.Automatic.ordinal());
        return MeasurementUnitsSetting.values()[ordinal];
    }

    public static void setUnitsSetting(MeasurementUnitsSetting setting) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("unitsetting", setting.ordinal());
        edit.commit();
    }

    public static void setBackgroundNavigation(boolean b) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("backnav", b);
        edit.commit();
    }

    public static boolean getBackgroundNavigation() {
        return prefs.getBoolean("backnav", false);
    }

    public static void setShowScaleOnMap(boolean b) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("showscale", b);
        editor.commit();
    }

    public static boolean getShowScaleOnMap() {
        return prefs.getBoolean("showscaleonmap", false);
    }

    public static void setTiltMap(boolean b) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("tilt", b);
        edit.commit();
    }

    public static boolean getTiltMap() {
        return prefs.getBoolean("tilt", false);
    }
}
