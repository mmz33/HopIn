package aub.hopin;

public class UserInfo {
    public String firstName;
    public String lastName;
    public String email;
    public int age;
    public UserGender gender;
    public UserMode mode;

    public String phoneNumber;
    public String vehicleType;
    public String status;

    public int maximumPassengerCount;

    public String profilePicturePath;
    public String schedulePath;

    public UserInfo() {
        this.firstName = "default";
        this.lastName = "default";
        this.email = "default";
        this.age = 0;
        this.gender = UserGender.Unspecified;
        this.mode = UserMode.Unspecified;

        this.phoneNumber = "default";
        this.vehicleType = "default";
        this.status = "Hello, I'm using HopIn!";
        this.maximumPassengerCount = 0;
        this.profilePicturePath = "default";
        this.schedulePath = "default";
    }
}
