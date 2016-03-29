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

    public String phoneNumber;
    public String status;

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
        phoneNumber = "";
        status = "";
        profileImage = null;
        scheduleImage = null;
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
                Log.e("", "Started loader with no email.");
                return;
            } else {
                HashMap<String, String> response = Server.queryUserInfo(info.email);
                info.firstName = response.get("firstname");
                info.lastName = response.get("lastname");
                info.email = response.get("email");
                info.age = Integer.parseInt(response.get("age"));
                String gender = response.get("gender");
                String mode = response.get("mode");
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
                info.phoneNumber = response.get("phone");
                info.status = response.get("status");
                info.profileImage = ResourceManager.getProfileImage(info.email);
                info.scheduleImage = ResourceManager.getScheduleImage(info.email);
                info.infoValid = true;
            }
        }
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
