package aub.hopin;

public enum UserState {
    Passive,
    Offering,
    Wanting;
    public static UserState[] val = UserState.values();

    public static UserState fromSymbol(String s) {
        switch (s) {
            case "P": return Passive;
            case "O": return Offering;
            case "W": return Wanting;
            default: throw new IllegalArgumentException();
        }
    }

    public static String toSymbol(UserState s) {
        switch (s) {
            case Passive: return "P";
            case Offering: return "O";
            case Wanting: return "W";
            default: throw new IllegalArgumentException();
        }
    }
}
