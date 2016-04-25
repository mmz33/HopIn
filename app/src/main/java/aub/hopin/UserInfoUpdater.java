package aub.hopin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserInfoUpdater {
    private static HashMap<String, UserInfo> userInfoHashMap = new HashMap<>();
    private static Timer updateTimer = null;
    private static TimerTask updateTask = null;

    public static void start() {
        updateTimer = new Timer();
        updateTask = new TimerTask() {
            public void run() {
                ArrayList<String> emails = new ArrayList<>();
                for (String email : userInfoHashMap.keySet()) {
                    emails.add(email);
                }

                ArrayList<HashMap<String, String>> response = null;

                try {
                    response = Server.queryUsersInfo(emails);
                } catch (ConnectionFailureException e) {}

                if (response == null)
                    return;

                for (HashMap<String, String> map : response) {
                    String email = map.get("email");
                    UserInfo info = userInfoHashMap.get(email);
                    UserInfoLoader.updateFromServerResponse(info, map);
                }
            }
        };
        updateTimer.scheduleAtFixedRate(updateTask, 1000, 1000);
    }

    public static void stop() {
        updateTimer.cancel();
    }

    public static void requestPeriodicUpdates(UserInfo info) {
        if (info == null) throw new IllegalArgumentException();
        userInfoHashMap.put(info.email, info);
    }

    public static void remove(UserInfo info) {
        if (info == null) throw new IllegalArgumentException();
        String email = info.email;
        if (userInfoHashMap.containsKey(email)) {
            userInfoHashMap.remove(email);
        }
    }

    public static void clear() {
        userInfoHashMap.clear();
    }
}
