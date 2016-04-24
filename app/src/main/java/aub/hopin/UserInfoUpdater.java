package aub.hopin;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserInfoUpdater {
    private static HashMap<String, UserInfo> userInfoHashMap = new HashMap<>();
    private static TimerTask updateTask = null;

    static {
        Timer timer = new Timer();
        updateTask = new TimerTask() {
            public void run() {
                for (String email : userInfoHashMap.keySet()) {
                    try {
                        HashMap<String, String> response = Server.queryUserInfo(email);
                        UserInfo info = userInfoHashMap.get(email);
                        UserInfoLoader.updateFromServerResponse(info, response);
                    } catch (ConnectionFailureException e) {}
                }
            }};
        timer.scheduleAtFixedRate(updateTask, 0, 750);
    }

    public static void requestPeriodicUpdates(UserInfo info) {
        userInfoHashMap.put(info.email, info);
    }

    public static void remove(UserInfo info) {
        String email = info.email;
        if (userInfoHashMap.containsKey(email)) {
            userInfoHashMap.remove(email);
        }
    }

    public static void clear() {
        userInfoHashMap.clear();
    }
}
