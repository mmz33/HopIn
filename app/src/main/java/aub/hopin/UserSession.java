package aub.hopin;

public class UserSession {
    private static UserSession activeSession = null;

    private UserInfo info;
    private long sessionId_1;
    private long sessionId_2;

    public UserSession(UserInfo info) {
        this.info = info;
        this.sessionId_1 = -1;
        this.sessionId_2 = -1;
    }

    public boolean valid() {
        return this.sessionId_1 != -1
            && this.sessionId_2 != -1;
    }

    public long[] getSessionId() {
        long[] id = new long[2];
        id[0] = this.sessionId_1;
        id[1] = this.sessionId_2;
        return id;
    }

    public UserInfo getUserInfo() {
        return this.info;
    }

    // Returns the active session in the application.
    public static UserSession getActiveSession() {
        return activeSession;
    }

    // Sets the active session in the application.
    public static void setActiveSession(UserSession session) {
        activeSession = session;
    }
}
