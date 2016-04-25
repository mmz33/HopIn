package aub.hopin;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserMapMarkerUpdater {
    private static HashMap<String, UserMapMarker> userMarkerMap = new HashMap<>();
    private static Timer updateTimer = null;

    public static void start() {
        updateTimer = new Timer();
        TimerTask updateTask = new TimerTask() {
            public void run() {
                for (String email : userMarkerMap.keySet()) {
                    UserInfo info = UserInfoFactory.get(email);
                    if (info == null) continue;

                    UserMapMarker marker = userMarkerMap.get(email);
                    if (marker == null) continue;

                    marker.updateImage();
                    marker.setPosition(info.latitude, info.longitude);
                }
            }
        };
        updateTimer.scheduleAtFixedRate(updateTask, 1000, 1000);
    }

    public static void stop() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        if (userMarkerMap != null) {
            userMarkerMap.clear();
        }
    }

    public static void requestPeriodicUpdates(UserMapMarker marker) {
        if (marker != null) {
            String email = marker.getEmail();
            userMarkerMap.put(email, marker);
        }
    }

    public static void remove(UserMapMarker marker) {
        if (marker != null) {
            String email = marker.getEmail();
            userMarkerMap.remove(email);
        }
    }
}
