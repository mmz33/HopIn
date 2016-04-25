package aub.hopin;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserMapMarkerUpdater {
    private static HashMap<String, UserMapMarker> userMarkerMap = new HashMap<>();
    private static HashMap<String, String> currentHashes = new HashMap<>();
    private static Timer updateTimer = null;

    public static void start() {
        updateTimer = new Timer();
        TimerTask updateTask = new TimerTask() {
            public void run() {
                for (String email : userMarkerMap.keySet()) {
                    UserInfo info = UserInfoFactory.get(email);
                    UserMapMarker marker = userMarkerMap.get(email);

                    assert info != null;
                    assert marker != null;

                    String oldHash = currentHashes.get(email);
                    String curHash = info.getProfileImageHash();
                    if (!oldHash.equals(curHash)) {
                        currentHashes.put(email, curHash);
                        marker.updateImage();
                    }

                    marker.setPosition(info.latitude, info.longitude);
                }
            }
        };
        updateTimer.scheduleAtFixedRate(updateTask, 1000, 1000);
    }

    public static void stop() {
        updateTimer.cancel();
        currentHashes.clear();
        userMarkerMap.clear();
    }

    public static void requestPeriodicUpdates(UserMapMarker marker) {
        if (marker != null) {
            String email = marker.getEmail();
            UserInfo info = UserInfoFactory.get(email);
            currentHashes.put(email, info.getProfileImageHash());
            userMarkerMap.put(email, marker);
        }
    }

    public static void remove(UserMapMarker marker) {
        if (marker != null) {
            String email = marker.getEmail();
            userMarkerMap.remove(email);
            currentHashes.remove(email);
        }
    }
}
