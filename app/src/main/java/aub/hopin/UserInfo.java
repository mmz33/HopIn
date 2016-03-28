package aub.hopin;

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

    public String localProfilePicturePath;
    public String localSchedulePicturePath;

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
        localProfilePicturePath = "";
        localSchedulePicturePath = "";
    }

    public UserInfo(String email) {
        this();
        load(email);
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
            }
        }
    }

    public void load(String email) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String response = Server.queryUserInfo(email);
            }
        });
    }

    public boolean valid() {
        return infoValid;
    }
}
