package aub.hopin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserInfoUpdater {
    private static HashMap<String, UserInfo> userInfoHashMap = new HashMap<>();
    private static Timer updateTimer = null;
    private static TimerTask updateTask = null;
    private static ArrayList<Runnable> onUpdateCallbacks = new ArrayList<>();

    public static void start() {
        updateTimer = new Timer();
        updateTask = new TimerTask() {
            public void run() {
                ArrayList<String> emails = new ArrayList<>();
                for (String email : userInfoHashMap.keySet()) {
                    emails.add(email);
                }
                try {
                    ArrayList<HashMap<String, String>> response = Server.queryUsersInfo(emails);

                    for (HashMap<String, String> map : response) {
                        String email = map.get("email");
                        UserInfo info = userInfoHashMap.get(email);
                        UserInfoLoader.updateFromServerResponse(info, map);
                    }
                } catch (ConnectionFailureException e) {}

                try {
                    for (Runnable f : onUpdateCallbacks) {
                        f.run();
                    }
                } catch (Throwable t) {}
            }
        };
        updateTimer.scheduleAtFixedRate(updateTask, 1000, 1000);
    }

    public static void registerOnUpdate(Runnable f) {
        onUpdateCallbacks.add(f);
    }

    public static void stop() {
        updateTimer.cancel();
    }

    public static void requestPeriodicUpdates(UserInfo info) {
        if (info == null) return;
        userInfoHashMap.put(info.email, info);
    }

    public static void remove(UserInfo info) {
        if (info == null) return;

        String email = info.email;
        if (userInfoHashMap.containsKey(email)) {
            userInfoHashMap.remove(email);
        }
    }

    public static void clear() {
        userInfoHashMap.clear();
    }
}
