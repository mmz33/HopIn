package aub.hopin;

public class ActiveUser {
    private static UserInfo info = null;
    private static String sessionId = "";

    /**
     * Returns the information object of the
     * active user.
     * @return  the information object of the active user.
     */
    public static UserInfo getInfo() {
        return info;
    }

    /**
     * Returns the email of the active user.
     * This method is just for convenience.
     *
     * @return the email of the active user
     */
    public static String getEmail() {
        return getInfo().email;
    }

    /**
     * Sets the current active user information object.
     * This will keep a reference for the entire application to use.
     *
     * @param i  the info object
     */
    public static void setInfo(UserInfo i) {
        info = i;
    }

    /**
     * Returns the current session id of the active user.
     *
     * @return  the session id
     */
    public static String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the current session id of the active user.
     *
     * @param id  the session id
     */
    public static void setSessionId(String id) {
        sessionId = id;
    }

    /**
     * Clears the current session of the active user and
     * his information object. This is to be called when
     * the user is no longer considered 'the' active user.
     */
    public static void clearSession() {
        sessionId = "";
        info = null;
    }
}
