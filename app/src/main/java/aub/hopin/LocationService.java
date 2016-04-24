package aub.hopin;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import java.util.Timer;
import java.util.TimerTask;

public class LocationService implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private GoogleApiClient client = null;
    private Location lastKnownLocation = null;
    private Timer locationSender = null;

    // Instance retrieval for this singleton class.
    private static LocationService instance = null;
    public static LocationService getInstance() {
        if (instance == null) {
            instance = new LocationService();
        }
        return instance;
    }

    // Updates the location.
    public void onLocationChanged(Location location) { lastKnownLocation = location; }
    public void onConnectionSuspended(int i) {}
    public void onConnected(Bundle bundle) {}
    public void onConnectionFailed(ConnectionResult result) {}

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
    public void start() {
        Context context = GlobalContext.get();

        client = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();

        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(200);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
            LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            String provider = manager.getBestProvider(new Criteria(), true);
            lastKnownLocation = manager.getLastKnownLocation(provider);
        } catch (SecurityException e) {
            Toast.makeText(context, "No Permission For Location Services.", Toast.LENGTH_LONG).show();
        }

        locationSender = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Location location = getCurrentLocation();
                if (location != null) new AsyncSendPosition(location).execute();
            }
        };
        locationSender.scheduleAtFixedRate(task, 0, 1000);
    }

    public void stop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        locationSender.cancel();
    }

    public Location getCurrentLocation() {
        return lastKnownLocation;
    }
}
