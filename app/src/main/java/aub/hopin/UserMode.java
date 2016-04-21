package aub.hopin;

public enum UserMode {
    Unspecified,
    DriverMode,
    PassengerMode;
    public static UserMode[] val = UserMode.values();

    public static UserMode fromSymbol(String s) {
        switch (s) {
            case "D": return UserMode.DriverMode;
            case "P": return UserMode.PassengerMode;
            case "U": return UserMode.Unspecified;
            default: throw new IllegalArgumentException();
        }
    }

    public static String toSymbol(UserMode m) {
        switch (m) {
            case DriverMode: return "D";
            case PassengerMode: return "P";
            case Unspecified: return "U";
            default: throw new IllegalArgumentException();
        }
    }
}