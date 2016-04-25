package aub.hopin;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.*;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.location.LocationListener;

import java.util.Timer;
import java.util.TimerTask;

public class LocationSender {
    private static Location lastKnownLocation = null;
    private static Timer locationSender = null;

    public static void setCurrentLocation(Location loc) {
        lastKnownLocation = new Location(loc);
    }

    public static void start() {
        locationSender = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                UserInfo info = ActiveUser.getInfo();
                if (info == null) return;
                if (lastKnownLocation == null) return;

                try {
                    if (Server.sendGlobalPosition(info.email, lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude()).equals("OK")) {
                        info.longitude = lastKnownLocation.getLongitude();
                        info.latitude = lastKnownLocation.getLatitude();
                    }
                } catch (ConnectionFailureException e) {}
            }
        };
        locationSender.scheduleAtFixedRate(task, 1000, 1000);
    }

    public static void stop() {
        if (locationSender != null) {
            locationSender.cancel();
        }
    }
}
