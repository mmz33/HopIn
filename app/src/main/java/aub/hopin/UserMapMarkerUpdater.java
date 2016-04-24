package aub.hopin;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserMapMarkerUpdater {
    private static HashMap<String, UserMapMarker> userMarkerMap = new HashMap<>();
    private static HashMap<String, String> currentHashes = new HashMap<>();
    private static TimerTask updateTask = null;

    static {
        Timer timer = new Timer();
        updateTask = new TimerTask() {
            public void run() {
                for (String email : userMarkerMap.keySet()) {
                    UserInfo info = UserInfoFactory.get(email);
                    info.lock();
                    String oldHash = currentHashes.get(email);
                    String curHash = info.getProfileImageHash();
                    if (!oldHash.equals(curHash)) {
                        UserMapMarker marker = userMarkerMap.get(email);
                        marker.updateImage();
                    }
                    currentHashes.put(email, curHash);
                    info.unlock();
                }
            }};
        timer.scheduleAtFixedRate(updateTask, 0, 750);
    }

    public static void requestPeriodicUpdates(UserMapMarker marker) {
        String email = marker.getEmail();
        UserInfo info = UserInfoFactory.get(email);
        userMarkerMap.put(email, marker);
        currentHashes.put(email, info.getProfileImageHash());
    }

    public static void remove(UserMapMarker marker) {
        String email = marker.getEmail();
        userMarkerMap.remove(email);
        currentHashes.remove(email);
    }

    public static void clear() {

    }
}
