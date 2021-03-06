package aub.hopin;

public enum UserGender {
    Male,
    Female,
    Other;
    public static UserGender[] val = UserGender.values();

    public static UserGender fromSymbol(String s) {
        switch (s) {
            case "F": return UserGender.Female;
            case "M": return UserGender.Male;
            case "O": return UserGender.Other;
            default: throw new IllegalArgumentException();
        }
    }

    public static String toSymbol(UserGender g) {
        switch (g) {
            case Male: return "M";
            case Female: return "F";
            case Other: return "O";
            default: throw new IllegalArgumentException();
        }
    }
}