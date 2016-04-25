package aub.hopin;

public enum UserMode {
    DriverMode,
    PassengerMode;
    public static UserMode[] val = UserMode.values();

    public static UserMode fromSymbol(String s) {
        switch (s) {
            case "D": return UserMode.DriverMode;
            case "P": return UserMode.PassengerMode;
            default: throw new IllegalArgumentException();
        }
    }

    public static String toSymbol(UserMode m) {
        switch (m) {
            case DriverMode: return "D";
            case PassengerMode: return "P";
            default: throw new IllegalArgumentException();
        }
    }
}