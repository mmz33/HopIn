package aub.hopin;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserInfo {
    private Bitmap profileImage = null;
    private String profileHash  = "";

    public String firstName = "";
    public String lastName  = "";
    public String email     = "";
    public int age          = 0;

    public UserGender gender = UserGender.Other;
    public UserMode mode     = UserMode.PassengerMode;
    public UserState state   = UserState.Passive;
    public UserRole role     = UserRole.Student;

    public String phoneNumber = "";
    public String address     = "";
    public String poBox       = "";
    public String status      = "";

    public boolean showingAddress = false;
    public boolean showingPhone   = false;

    public Vehicle vehicle         = null;
    public Runnable onLoadCallback = null;

    public double longitude = 0.0;
    public double latitude  = 0.0;
    public boolean active   = true;

    public double curDestinationLatitude = 0.0;
    public double curDestinationLongitude = 0.0;

    public boolean prefsWithMen = true;
    public boolean prefsWithWomen = true;
    public boolean prefsWithStudents = true;
    public boolean prefsWithTeachers = true;

    public double notificationRadius = 5.0;
    public boolean hasDestination = false;

    public UserInfo(String email, boolean blocking, Runnable onLoad) {
        this.onLoadCallback = onLoad;
        this.vehicle = new Vehicle(email);
        this.email = email;

        Runnable loader = new UserInfoLoader(this);
        if (blocking) {
            loader.run();
        } else {
            Thread thread = new Thread(loader);
            thread.start();
        }
    }

    public UserInfo(String email, boolean blocking) {
        this(email, blocking, null);
    }
    public UserInfo(String email) {
        this(email, false);
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(Bitmap bitmap) {
        profileImage = ImageUtils.makeRounded(bitmap);
    }

    public String getProfileImageHash() {
        return profileHash;
    }
    public void setProfileImageHash(String hash) {
        profileHash = hash;
    }
}
