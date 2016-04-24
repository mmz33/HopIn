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
    private boolean infoValid   = false;
    private Semaphore semaphore = new Semaphore(1);
    public Bitmap profileImage = null;
    private String profileHash  = "";

    public String firstName = "";
    public String lastName  = "";
    public String email     = "";
    public int age          = 0;

    public UserGender gender = UserGender.Unspecified;
    public UserMode mode     = UserMode.Unspecified;
    public UserState state   = UserState.Passive;
    public UserRole role     = UserRole.Unspecified;

    public String phoneNumber = "";
    public String address     = "";
    public String poBox       = "";
    public String status      = "";

    public boolean showingAddress = false;
    public boolean showingPhone   = false;

    public Vehicle vehicle         = null;
    public Runnable onLoadCallback = null;

    public double longitude;
    public double latitude;

    public boolean active;

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

    // Determines if the information inside this UserInfo object
    // is valid. Valid means that there exists a time t such that
    // the info inside this UserInfo object was the information
    // of the user at time t.
    public boolean isValid() {
        return infoValid;
    }

    public void validate() {
        infoValid = true;
    }

    public void lock() {
        try { semaphore.acquire(); } catch (InterruptedException e) {}
    }

    public void unlock() {
        semaphore.release();
    }
}
