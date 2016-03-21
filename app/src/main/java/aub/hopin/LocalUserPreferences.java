package aub.hopin;

public class LocalUserPreferences {
    private static MeasurementUnits unit = MeasurementUnits.Metric;
    private static boolean backgroundNavigation = false;
    private static boolean tiltMap = false;

    public static void setUnit(MeasurementUnits newUnit) {
        unit = newUnit;
    }
    public static MeasurementUnits getUnit() {
        return unit;
    }

    public static void setBackgroundNavigationOn() {
        backgroundNavigation = true;
    }
    public static void setBackgroundNavigationOff() {
        backgroundNavigation = false;
    }
    public static boolean getBackgroudNavigation() {
        return backgroundNavigation;
    }

    public static void setTiltMapOn() {
        tiltMap = true;
    }
    public static void setTiltMapOff() {
        tiltMap = false;
    }
    public static boolean getTiltMap() {
        return tiltMap;
    }
}
