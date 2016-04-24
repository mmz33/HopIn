package aub.hopin;

import android.location.GpsStatus;

import java.util.ArrayList;
import java.util.List;

public abstract class ModeSwitchEvent {
    private static List<ModeSwitchListener> listeners = new ArrayList<ModeSwitchListener>();

    public static void fire(String email) {
        for (ModeSwitchListener listener : listeners) {
            listener.onSwitch(email);
        }
    }

    public static void register(ModeSwitchListener listener) {
        listeners.add(listener);
    }
}
