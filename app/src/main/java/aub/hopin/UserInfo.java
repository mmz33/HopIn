package aub.hopin;

import android.graphics.Bitmap;
import android.util.Log;
import java.util.HashMap;

public class UserInfo {
    private boolean infoValid;

    public String firstName;
    public String lastName;
    public String email;
    public int age;
    public UserGender gender;

    public UserMode mode;
    public UserState state;

    public String phoneNumber;
    public String address;
    public String poBox;
    public String status;

    public boolean showingAddress;
    public boolean showingPhone;

    public Bitmap profileImage;
    public Bitmap scheduleImage;

    public UserInfo() {
        infoValid = false;
        firstName = "";
        lastName = "";
        email = "";
        age = 0;
        gender = UserGender.Unspecified;
        mode = UserMode.Unspecified;
        state = UserState.Passive;
        phoneNumber = "";
        address = "";
        poBox = "";
        status = "";
        profileImage = null;
        scheduleImage = null;
        showingAddress = false;
        showingPhone = false;
    }

    public UserInfo(String email, boolean blocking) {
        this();
        load(email, blocking);
    }

    public UserInfo(String email) {
        this(email, false);
    }

    private static class Loader implements Runnable {
        private UserInfo info;
        public Loader(UserInfo info) {
            this.info = info;
        }
        public void run() {
            if (info.email.equals("")) {
                Log.e("error", "Started loader with no email.");
                return;
            } else {
                HashMap<String, String> response = Server.queryUserInfo(info.email);
                info.firstName = response.get("firstname");
                info.lastName = response.get("lastname");
                info.email = response.get("email");
                info.age = Integer.parseInt(response.get("age"));
                String gender = response.get("gender");
                String mode = response.get("mode");
                String state = response.get("state");
                switch (gender) {
                    case "F": info.gender = UserGender.Female; break;
                    case "M": info.gender = UserGender.Male; break;
                    case "O": info.gender = UserGender.Other; break;
                    default:  info.gender = UserGender.Unspecified; break;
                }
                switch (mode) {
                    case "D": info.mode = UserMode.DriverMode; break;
                    case "P": info.mode = UserMode.PassengerMode; break;
                    default:  info.mode = UserMode.Unspecified; break;
                }
                switch (state) {
                    case "P": info.state = UserState.Passive; break;
                    case "O": info.state = UserState.Offering; break;
                    case "W": info.state = UserState.Wanting; break;
                    default:  info.state = UserState.Passive; break;
                }
                info.phoneNumber = response.get("phone");
                info.status = response.get("status");
                info.address = response.get("address");
                info.poBox = response.get("pobox");
                info.showingAddress = response.get("showaddress").equals("1");
                info.showingPhone = response.get("showphone").equals("1");
                info.scheduleImage = ResourceManager.getScheduleImage(info.email);
                info.setProfileImage(ResourceManager.getProfileImage(info.email));
                info.infoValid = true;
            }
        }
    }

    public void setProfileImage(Bitmap bitmap) {
        profileImage = ImageUtils.makeRounded(bitmap);
    }

    public void load(String email, boolean blocking) {
        this.email = email;
        Runnable loader = new Loader(this);
        if (blocking) {
            loader.run();
        } else {
            Thread thread = new Thread(loader);
            thread.start();
        }
    }

    public void load(String email) {
        load(email, false);
    }

    public boolean valid() {
        return infoValid;
    }
}
