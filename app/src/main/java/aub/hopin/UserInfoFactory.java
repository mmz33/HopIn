package aub.hopin;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class UserInfoFactory {
    private static HashMap<String, UserInfo> userInfoMap = new HashMap<>();

    // Returns a UserInfo object for the given email.
    // This may retrieve an unloaded user info object.
    public static UserInfo get(String email) {
        if (userInfoMap.containsKey(email)) {
            return userInfoMap.get(email);
        } else {
            UserInfo info = new UserInfo(email, false);
            userInfoMap.put(email, info);
            return info;
        }
    }
}
