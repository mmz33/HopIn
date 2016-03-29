package aub.hopin;

public class ActiveUser {
    private static UserInfo info = null;

    public static UserInfo getActiveUserInfo() { return info; }
    public static String getEmail() { return getActiveUserInfo().email; }
    public static void setActiveUserInfo(UserInfo i) { info = i; }
}
