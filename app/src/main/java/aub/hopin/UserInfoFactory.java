package aub.hopin;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserInfoFactory {
    private static HashMap<String, UserInfo> userInfoMap = new HashMap<>();

    // Returns a UserInfo object for the given email.
    // This may retrieve an unloaded user info object.
    public static UserInfo get(String email) {
        return get(email, false);
    }

    public static UserInfo get(String email, boolean blocking) {
        if (userInfoMap.containsKey(email)) {
            return userInfoMap.get(email);
        } else {
            UserInfo info = new UserInfo(email, blocking);
            userInfoMap.put(email, info);
            return info;
        }
    }

    public static UserInfo get(String email, String content) {
        UserInfo info = new UserInfo(email, content);
        userInfoMap.put(email, info);
        return info;
    }

    public static UserInfo get(String email, HashMap<String, String> response) {
        UserInfo info = new UserInfo(email, response);
        userInfoMap.put(email, info);
        return info;
    }

    public static void clear() {
        userInfoMap.clear();
    }
}
