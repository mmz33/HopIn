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

    public String role;
    public Vehicle vehicle;

    public Runnable onLoadCallback;

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
        role = "";
        vehicle = null;
        showingAddress = false;
        showingPhone = false;
        onLoadCallback = null;
    }

    public UserInfo(String email, boolean blocking, Runnable onLoad) {
        this();
        onLoadCallback = onLoad;
        load(email, blocking);
    }

    public UserInfo(String email, boolean blocking) {
        this(email, blocking, null);
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
                try {
                    HashMap<String, String> response = Server.queryUserInfo(info.email);
                    info.firstName = response.get("firstname");
                    info.lastName = response.get("lastname");
                    info.email = response.get("email");
                    info.age = Integer.parseInt(response.get("age"));
                    info.gender = UserGender.fromSymbol(response.get("gender"));
                    info.mode = UserMode.fromSymbol(response.get("mode"));
                    info.state = UserState.fromSymbol(response.get("state"));
                    info.phoneNumber = response.get("phone");
                    info.status = response.get("status");
                    info.address = response.get("address");
                    info.poBox = response.get("pobox");
                    info.showingAddress = response.get("showaddress").equals("1");
                    info.showingPhone = response.get("showphone").equals("1");
                    info.scheduleImage = ResourceManager.getScheduleImage(info.email);
                    info.setProfileImage(ResourceManager.getProfileImage(info.email));
                    info.role = response.get("role");
                    int capacity = Integer.parseInt(response.get("vehiclecapacity"));
                    String make = response.get("vehiclemake");
                    String color = response.get("vehiclecolor");
                    info.vehicle = new Vehicle(capacity, make, color, info.email);
                    info.infoValid = true;

                    if (info.onLoadCallback != null) {
                        info.onLoadCallback.run();
                    }
                } catch (ConnectionFailureException e) {
                    // TODO
                    // Handle this exception somehow?
                }
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
