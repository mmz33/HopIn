package aub.hopin;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserMapMarkerUpdater {
    private static HashMap<String, UserMapMarker> userMarkerMap = new HashMap<>();
    private static HashMap<String, String> currentHashes = new HashMap<>();
    private static Timer updateTimer = null;
    private static TimerTask updateTask = null;

    public static void start() {
        updateTimer = new Timer();
        updateTask = new TimerTask() {
            public void run() {
                for (String email : userMarkerMap.keySet()) {
                    UserInfo info = UserInfoFactory.get(email);
                    UserMapMarker marker = userMarkerMap.get(email);

                    assert info != null;
                    assert marker != null;

                    String oldHash = currentHashes.get(email);
                    String curHash = info.getProfileImageHash();
                    if (oldHash != null && curHash != null && !oldHash.equals(curHash)) {
                        marker.updateImage();
                        currentHashes.put(email, curHash);
                    }

                    marker.setPosition(info.latitude, info.longitude);
                }
            }
        };
        updateTimer.scheduleAtFixedRate(updateTask, 1000, 3000);
    }

    public static void stop() {
        currentHashes.clear();
        userMarkerMap.clear();
        updateTimer.cancel();
        updateTask = null;
    }

    public static void requestPeriodicUpdates(UserMapMarker marker) {
        if (marker != null) {
            String email = marker.getEmail();
            UserInfo info = UserInfoFactory.get(email);

            assert info.getProfileImageHash() != null;

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
