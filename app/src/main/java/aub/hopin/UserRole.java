package aub.hopin;


public enum UserRole {
    Student,
    Professor,
    Unspecified;

    public static UserRole[] val = UserRole.values();

    public static UserRole fromSymbol(String s) {
        switch (s) {
            case "S": return Student;
            case "P": return Professor;
            default: throw new IllegalArgumentException();
        }
    }

    public static String toSymbol(UserRole s) {
        switch (s) {
            case Student: return "S";
            case Professor: return "P";
            default: return "?";
        }
    }
}
