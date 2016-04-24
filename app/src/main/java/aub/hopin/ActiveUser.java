package aub.hopin;

public class ActiveUser {
    private static UserInfo info = null;
    private static String sessionId = "";

    public static UserInfo getInfo() {
        return info;
    }

    public static String getEmail() {
        return getInfo().email;
    }

    public static void setInfo(UserInfo i) {
        info = i;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String id) {
        sessionId = id;
    }

    public static void clearSession() {
        sessionId = "";
        info = null;
    }
}
