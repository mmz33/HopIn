package aub.hopin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class SlideMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{

    private SupportMapFragment supportMapFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private GoogleMap mMap;
    private android.support.v4.app.FragmentManager sFm;

    private HashMap<String, UserInfo> userInfoMap;
    private HashMap<String, GroundOverlay> userMarkers;

    private HashMap<String, GroundOverlayOptions> asyncToCreate;
    private HashMap<String, GroundOverlayOptions> asyncToUpdate;
    private HashMap<String, UserInfo> asyncToDownload;
    private ArrayList<String> pendingDownloads;

    private LocationManager locationManager;
    private Location lastKnownLocation;
    private String provider;

    private Semaphore semaphore;

    // Gets the last known location using
    // the location services of the device.
    private Location getCurrentLocation() {
        return lastKnownLocation;
    }

    // Asynchronous position sending.
    // This sends the server the latest GPS position
    // of the device.
    private class AsyncSendPosition extends AsyncTask<Void, Void, Void> {
        private String email;
        private Location location;
        public AsyncSendPosition(Location location) {
            this.email = ActiveUser.getEmail();
            this.location = location;
        }
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Void doInBackground(Void... params) {
            try {
                if (!Server.sendGlobalPosition(email, location.getLongitude(), location.getLatitude()).equals("OK")) {
                    Log.e("error", "failed to send positioning info.");
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class AsyncDownloadProfileImage extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        public AsyncDownloadProfileImage(UserInfo info) {
            this.info = info;
        }
        protected void onPreExecute() { super.onPreExecute(); }
        protected Void doInBackground(Void... params) {
            ResourceManager.setProfileImageDirty(info.email);
            info.setProfileImage(ResourceManager.getProfileImage(info.email));
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            info.updating.set(false);
        }
    }

    // Asynchronous marker setup. This will update the
    // markers on the map according to the locations
    // of the active users as specified by the server.
    private class AsyncSetupMarkers extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Void doInBackground(Void... params) {
            try {
                HashMap<String, String> activeUserData = Server.queryActiveUserPositionsAndImageHashes();
                if (activeUserData != null) {
                    Handler handler = new Handler(getApplicationContext().getMainLooper());

                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {}

                    asyncToCreate.clear();
                    asyncToUpdate.clear();
                    asyncToDownload.clear();

                    for (String email : activeUserData.keySet()) {
                        if (!userInfoMap.containsKey(email))
                            userInfoMap.put(email, new UserInfo(email, false));

                        UserInfo uInfo = userInfoMap.get(email);
                        if (uInfo == null) continue;
                        if (uInfo.profileImage == null) continue;
                        if (uInfo.updating.get()) continue;

                        String data[] = activeUserData.get(email).split(" ");
                        String lat = data[0];
                        String lon = data[1];
                        String imHash = data[2];

                        if (!uInfo.profileHash.equals(imHash)) {
                            Log.e("error", "Hashes don't match.");
                            uInfo.updating.set(true);
                            asyncToDownload.put(email, uInfo);
                            pendingDownloads.add(uInfo.email);
                            uInfo.profileHash = imHash;
                            continue;
                        } else {
                            Log.e("error", "Hashes match");
                        }

                        LatLng target = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                        if (userMarkers.containsKey(email)) {
                            GroundOverlayOptions options = new GroundOverlayOptions()
                                    .position(target, 100f, 100f);
                            if (!uInfo.updating.get() && pendingDownloads.contains(uInfo.email)) {
                                String color = "";
                                switch (uInfo.state) {
                                    case Passive:  color = "#FFFFFF"; break;
                                    case Offering: color = "#00E500"; break;
                                    case Wanting:  color = "#E50000"; break;
                                }
                                options.image(BitmapDescriptorFactory.fromBitmap(ImageUtils.overlayRoundBorder(uInfo.profileImage, color)));
                                pendingDownloads.remove(uInfo.email);
                            }
                            asyncToUpdate.put(email, options);
                        } else {
                            String color = "";
                            switch (uInfo.state) {
                                case Passive:  color = "#FFFFFF"; break;
                                case Offering: color = "#00E500"; break;
                                case Wanting:  color = "#E50000"; break;
                            }
                            GroundOverlayOptions options = new GroundOverlayOptions()
                                    .image(BitmapDescriptorFactory.fromBitmap(ImageUtils.overlayRoundBorder(uInfo.profileImage, color)))
                                    .position(target, 100f, 100f);
                            asyncToCreate.put(email, options);
                        }
                    }

                    Runnable updater = new Runnable() {
                        public void run() {
                            try { semaphore.acquire(); } catch (InterruptedException e) {}
                            for (String email : asyncToCreate.keySet()) {
                                userMarkers.put(email, mMap.addGroundOverlay(asyncToCreate.get(email)));
                            }
                            for (String email : asyncToUpdate.keySet()) {
                                GroundOverlayOptions options = asyncToUpdate.get(email);
                                userMarkers.get(email).setPosition(options.getLocation());
                            }
                            for (String email : asyncToDownload.keySet()) {
                                new AsyncDownloadProfileImage(asyncToDownload.get(email)).execute();
                            }
                            semaphore.release();
                        }
                    };

                    semaphore.release();
                    handler.post(updater);
                } else {
                    Log.e("error", "failed to query active users.");
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class AsyncSendNotification extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        private UserState state;
        private boolean success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            info = ActiveUser.getInfo();
            if (info.mode == UserMode.PassengerMode) {
                state = UserState.Wanting;
            } else {
                state = UserState.Offering;
            }
            success = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (Server.sendStateSwitch(info.email, state).equals("OK")) {
                    info.state = state;
                    success = true;
                } else {
                    Log.e("error", "Something went wrong with sending notify message");
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!success) {
                //...
            }
        }
    }

    /*
    public void startRecording() {
        gpsTimer.cancel();
        gpsTimer = new Timer();
        long checkInterval = getGPSCheckMilliSecsFromPrefs();
        long minDistance = getMinDistanceFromPrefs();
        // receive updates
        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        for (String s : locationManager.getAllProviders()) {
            locationManager.requestLocationUpdates(s, checkInterval,
                    minDistance, new LocationListener() {

                        @Override
                        public void onStatusChanged(String provider,
                                                    int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(String provider) {}

                        @Override
                        public void onProviderDisabled(String provider) {}

                        @Override
                        public void onLocationChanged(Location location) {
                            // if this is a gps location, we can use it
                            if (location.getProvider().equals(
                                    LocationManager.GPS_PROVIDER)) {
                                doLocationUpdate(location, true);
                            }
                        }
                    });
            // //Toast.makeText(this, "GPS Service STARTED",
            // Toast.LENGTH_LONG).show();
            gps_recorder_running = true;
        }
        // start the gps receiver thread
        gpsTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Location location = getBestLocation();
                doLocationUpdate(location, false);
            }
        }, 0, checkInterval);
    }

    public void doLocationUpdate(Location l, boolean force) {
        long minDistance = getMinDistanceFromPrefs();
        Log.d(TAG, "update received:" + l);
        if (l == null) {
            Log.d(TAG, "Empty location");
            if (force)
                Toast.makeText(this, "Current location not available",
                        Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastLocation != null) {
            float distance = l.distanceTo(lastLocation);
            Log.d(TAG, "Distance to last: " + distance);
            if (l.distanceTo(lastLocation) < minDistance && !force) {
                Log.d(TAG, "Position didn't change");
                return;
            }
            if (l.getAccuracy() >= lastLocation.getAccuracy()
                    && l.distanceTo(lastLocation) < l.getAccuracy() && !force) {
                Log.d(TAG,
                        "Accuracy got worse and we are still "
                                + "within the accuracy range.. Not updating");
                return;
            }
            if (l.getTime() <= lastprovidertimestamp && !force) {
                Log.d(TAG, "Timestamp not never than last");
                return;
            }
        }
        // upload/store your location here
    }*/

    private ImageView imageView;
    private TextView userNameTextView;
    private TextView userEmailTextView;

    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);

        setContentView(R.layout.activity_main_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UserInfo info = ActiveUser.getInfo();

        userMarkers = new HashMap<>();
        userInfoMap = new HashMap<>();
        userInfoMap.put(ActiveUser.getEmail(), ActiveUser.getInfo());

        asyncToCreate = new HashMap<>();
        asyncToUpdate = new HashMap<>();
        asyncToDownload = new HashMap<>();
        pendingDownloads = new ArrayList<>();

        semaphore = new Semaphore(1);

        supportMapFragment = SupportMapFragment.newInstance();
        supportMapFragment.getMapAsync(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        sFm = getSupportFragmentManager();

        View hView = navigationView.getHeaderView(0);
        imageView = (ImageView)hView.findViewById(R.id.nav_header_image_view);
        userNameTextView = (TextView)hView.findViewById(R.id.nav_header_user_name);
        userEmailTextView = (TextView)hView.findViewById(R.id.nav_header_user_email);

        button1 = (Button)hView.findViewById(R.id.nav_header_main_button1);
        button2 = (Button)hView.findViewById(R.id.nav_header_main_button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button2.setText("Passive");
                button2.setBackgroundResource(R.color.colorGrey);
                if(button1.getText().toString().equals("Driver")) {
                    button1.setText("Passenger");
                    button1.setBackgroundResource(R.color.colorBlue);
                } else if(button1.getText().toString().equals("Passenger")){
                    button1.setText("Driver");
                    button1.setBackgroundResource(R.color.colorOrange);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button1.getText().toString().equals("Passenger")) {
                    if(button2.getText().equals("Wanting")) {
                        button2.setText("Passive");
                        button2.setBackgroundResource(R.color.colorGrey);
                    } else if(button2.getText().toString().equals("Passive")){
                        button2.setText("Wanting");
                        button2.setBackgroundResource(R.color.colorRed);
                    }
                } else if(button1.getText().toString().equals("Driver")) {
                    if(button2.getText().toString().equals("Passive")) {
                        button2.setText("Offering");
                        button2.setBackgroundResource(R.color.colorGreen);
                    } else if(button2.getText().toString().equals("Offering")) {
                        button2.setText("Passive");
                        button2.setBackgroundResource(R.color.colorGrey);
                    }
                }
            }
        });

        imageView.setImageBitmap(info.profileImage);
        userNameTextView.setText(info.firstName + " " + info.lastName);
        userEmailTextView.setText(info.email);

        // Show the map
        if (!supportMapFragment.isAdded())
            sFm.beginTransaction().add(R.id.content_frame, supportMapFragment).commit();
        sFm.beginTransaction().show(supportMapFragment).commit();

        // Get location services.
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);

        try {
            lastKnownLocation = locationManager.getLastKnownLocation(provider);
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "Failed to request location updates.", Toast.LENGTH_SHORT).show();
        }

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Location location = getCurrentLocation();
                if (location != null)
                    new AsyncSendPosition(location).execute();
                else
                    Log.e("error", "failed to get current location in periodic task.");
                new AsyncSetupMarkers().execute();
            }
        };
        t.scheduleAtFixedRate(task, 1000, 1000);
    }

    private class AsyncLogout extends AsyncTask<Void, Void, Void> {
        private boolean success;

        protected void onPreExecute() {
            super.onPreExecute();
            success = false;
        }

        protected Void doInBackground(Void... params) {
            try {
                if (Server.logout().equals("OK")) {
                    success = true;
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                Toast.makeText(getApplicationContext(), "Failed to logout.", Toast.LENGTH_SHORT);
            } else {
                SessionLoader.clean();
                finish();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (supportMapFragment.isAdded())
            sFm.beginTransaction().hide(supportMapFragment).commit();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(SlideMenu.this, Settings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(SlideMenu.this, ProfileSettings.class);
            intent.putExtra("email", ActiveUser.getEmail());
            startActivity(intent);
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_schedule) {
            startActivity(new Intent(SlideMenu.this, ScheduleSettings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(SlideMenu.this, FeedbackSettings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if(id == R.id.nav_vehicle_type) {
            startActivity(new Intent(SlideMenu.this, CarInfoSettings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if(id == R.id.nav_help) {
            startActivity(new Intent(SlideMenu.this, Help.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if(id == R.id.nav_about) {
            startActivity(new Intent(SlideMenu.this, About.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if(id == R.id.nav_terms_conditions) {
            startActivity(new Intent(SlideMenu.this, TermsAndConditions.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if(id == R.id.nav_contact_info) {
            startActivity(new Intent(SlideMenu.this, ContactInfoSettings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if(id == R.id.nav_ride_preferences) {
            startActivity(new Intent(SlideMenu.this, RidePreferences.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if(id == R.id.nav_logout) {
            new AsyncLogout().execute();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            //mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {}
        if (mMap != null) setUpMap();
        else             Log.e("error", "Map is not initialized!");
    }

    //make the current location as default
    public void setUpMap() {
        Location location = getCurrentLocation();
        if (location != null) {
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
