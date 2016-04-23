package aub.hopin;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public abstract class LocationServices {
    private static LocationManager locationManager;
    private static String locationProvider;
    private static Location lastKnownLocation;

    // Asynchronous position sending.
    // This sends the server the latest GPS position of the device.
    private static class AsyncSendPosition extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        private Location location;

        public AsyncSendPosition(Location location) {
            this.info = ActiveUser.getInfo();
            this.location = location;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                if (Server.sendGlobalPosition(info.email, location.getLongitude(), location.getLatitude()).equals("OK")) {
                    info.lock();
                    info.longitude = location.getLongitude();
                    info.latitude = location.getLatitude();
                    info.unlock();
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    // Starts up the location services sub system.
    public static void start(Context ctx) {
        locationManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        locationProvider = locationManager.getBestProvider(new Criteria(), true);

        try {
            lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        } catch (SecurityException e) {
            lastKnownLocation = null;
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lastKnownLocation = location;
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            Toast.makeText(ctx, "Failed to request location updates.", Toast.LENGTH_SHORT).show();
        }

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Location location = getCurrentLocation();
                if (location != null) new AsyncSendPosition(location).execute();
            }
        };
        t.scheduleAtFixedRate(task, 0, 1000);
    }

    public static Location getCurrentLocation() {
        return lastKnownLocation;
    }
}
