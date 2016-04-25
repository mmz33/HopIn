package aub.hopin;

import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class MainMap extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private SupportMapFragment supportMapFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private GoogleMap googleMap = null;
    private FragmentManager sFm;
    private GoogleApiClient apiClient;

    private Timer markerUpdater = new Timer();
    private boolean zoomedInYet = false;
    private HashMap<String, UserMapMarker> markers = new HashMap<>();
    private HashMap<String, UserMapDestinationMarker> destinationMarkers = new HashMap<>();
    private boolean currentlySettingDestination = false;
    private boolean currentlySettingMarkers = false;

    private ImageView slideMenuProfileImage;
    private TextView slideMenuUserName;
    private TextView slideMenuUserEmail;
    private Button userModeButton;
    private Button userStateButton;
    private LinearLayout selectDestination;

    // This will run whenever the application gets a location update
    // from the google service.
    public void onLocationChanged(Location loc) {
        if (loc == null) return;
        Toast.makeText(MainMap.this, "Updated location: " + loc.getLatitude() + " " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        LocationSender.setCurrentLocation(loc);
        if (!zoomedInYet && googleMap != null) {
            zoomedInYet = true;
            LatLng target = new LatLng(loc.getLatitude(), loc.getLongitude());
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        }
    }

    // Asynchronously sends the destination to the server.
    private class AsyncSendDestination extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        private boolean success;
        private double lat;
        private double lon;
        private double oldLat;
        private double oldLon;

        public AsyncSendDestination(UserInfo info, double latitude, double longitude) {
            this.info = info;
            success = false;
            lat = latitude;
            lon = longitude;
            oldLat = this.info.curDestinationLatitude;
            oldLon = this.info.curDestinationLongitude;
            info.curDestinationLatitude = lat;
            info.curDestinationLongitude = lon;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                success = Server.sendDestination(info.email, lat, lon).equals("OK");
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!success) {
                Toast.makeText(MainMap.this, "Failed to send destination!", Toast.LENGTH_SHORT).show();
                info.curDestinationLatitude = oldLat;
                info.curDestinationLongitude = oldLon;
            }
            currentlySettingDestination = false;
        }
    }

    private void switchToDriver() {
        switchToPassive();
        userModeButton.setText("Driver");
        userModeButton.setBackgroundResource(R.color.colorOrange);
    }

    private void switchToPassenger() {
        switchToPassive();
        userModeButton.setText("Passenger");
        userModeButton.setBackgroundResource(R.color.colorBlue);
    }

    private void switchToPassive() {
        userStateButton.setText("Passive");
        userStateButton.setBackgroundResource(R.color.colorGrey);
    }

    private void switchToOffering() {
        userStateButton.setText("Offering");
        userStateButton.setBackgroundResource(R.color.colorGreen);
    }

    private void switchToWanting() {
        userStateButton.setText("Wanting");
        userStateButton.setBackgroundResource(R.color.colorRed);
    }

    private void dropDownSelectDestination() {
        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))  {
            drawer.closeDrawer(GravityCompat.START);
        }
        selectDestination.setVisibility(LinearLayout.VISIBLE);
        selectDestination.startAnimation(slide_down);
        slide_down.setFillAfter(true);
        currentlySettingDestination = true;
    }

    // Sends a mode change to the server.
    private class AsyncStateModeChange extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        private boolean success;
        private UserMode mode;
        private UserState state;
        private UserMode oldMode;
        private UserState oldState;

        public AsyncStateModeChange(UserMode mode, UserState state) {
            this.info = ActiveUser.getInfo();
            this.mode = mode;
            this.state = state;
            this.success = false;
            this.oldMode = info.mode;
            this.oldState = info.state;

            info.mode = mode;
            info.state = state;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                success = Server.sendStateModeSwitch(info.email, mode, state).equals("OK");
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                Toast.makeText(MainMap.this, "Failed to switch state!", Toast.LENGTH_SHORT).show();
                info.mode = oldMode;
                info.state = oldState;

                switch (oldMode) {
                    case DriverMode:
                        switchToDriver();
                        break;
                    case PassengerMode:
                        switchToPassenger();
                        break;
                }
                switch (oldState) {
                    case Passive:
                        switchToPassive();
                        break;
                    case Wanting:
                        switchToWanting();
                        break;
                    case Offering:
                        switchToOffering();
                        break;
                }
            }
        }
    }

    protected void setupSlideMenu() {
        View hView = navigationView.getHeaderView(0);
        slideMenuProfileImage = (ImageView)hView.findViewById(R.id.nav_header_image_view);
        slideMenuUserName = (TextView)hView.findViewById(R.id.nav_header_user_name);
        slideMenuUserEmail = (TextView)hView.findViewById(R.id.nav_header_user_email);
        SlideMenuUpdater.start(slideMenuProfileImage);

        userModeButton = (Button)hView.findViewById(R.id.nav_header_main_button1);
        userStateButton = (Button)hView.findViewById(R.id.nav_header_main_button2);

        userModeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserInfo info = ActiveUser.getInfo();
                switch (info.mode) {
                    case PassengerMode:
                        switchToDriver();
                        new AsyncStateModeChange(UserMode.DriverMode, info.state).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    case DriverMode:
                        switchToPassenger();
                        new AsyncStateModeChange(UserMode.PassengerMode, info.state).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                }
            }
        });

        userStateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserInfo info = ActiveUser.getInfo();
                switch (info.mode) {
                    case PassengerMode:
                        switch (info.state) {
                            case Wanting:
                                switchToPassive();
                                UserMapDestinationMarker destinationMarker = destinationMarkers.get(info.email);
                                destinationMarker.destroy();
                                new AsyncStateModeChange(info.mode, UserState.Passive).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                            case Passive:
                                switchToWanting();
                                dropDownSelectDestination();
                                new AsyncStateModeChange(info.mode, UserState.Wanting).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                        }
                        break;
                    case DriverMode:
                        switch (info.state) {
                            case Offering:
                                switchToPassive();
                                UserMapDestinationMarker destinationMarker = destinationMarkers.get(info.email);
                                destinationMarker.destroy();
                                new AsyncStateModeChange(info.mode, UserState.Passive).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                            case Passive:
                                switchToOffering();
                                dropDownSelectDestination();
                                new AsyncStateModeChange(info.mode, UserState.Offering).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);
        setContentView(R.layout.activity_main_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        supportMapFragment = SupportMapFragment.newInstance();
        supportMapFragment.getMapAsync(this);
        selectDestination = (LinearLayout)findViewById(R.id.select_destination_layout);
        selectDestination.setVisibility(LinearLayout.GONE);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        sFm = getSupportFragmentManager();

        UserInfo info = ActiveUser.getInfo();
        if (info.getProfileImage() != null)
            slideMenuProfileImage.setImageBitmap(info.getProfileImage());

        slideMenuUserName.setText(info.firstName + " " + info.lastName);
        slideMenuUserEmail.setText(info.email);

        // Show the map
        if (!supportMapFragment.isAdded()) {
            sFm.beginTransaction().add(R.id.content_frame, supportMapFragment).commit();
        }
        sFm.beginTransaction().show(supportMapFragment).commit();


        markerUpdater = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                if (currentlySettingMarkers) return;
                currentlySettingMarkers = true;

                try {
                    ArrayList<String> activeUsers = Server.queryUsersOfInterest();

                    Log.e("users", "There are " + activeUsers.size() + " users of interest.");
                    for (int i = 0; i < activeUsers.size(); ++i) {
                        Log.e("users", activeUsers.get(i));
                    }

                    // Setup the markers for the new users of interest.
                    for (String email : activeUsers) {
                        UserInfo info = UserInfoFactory.get(email);

                        if (!markers.containsKey(email)) {
                            final String e = email;
                            MainMap.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    markers.put(e, new UserMapMarker(googleMap, e));
                                }
                            });
                        }
                    }

                    // Remove the markers for the users who are no longer of interest.
                    ArrayList<String> toRemove = new ArrayList<>();
                    for (String email : markers.keySet()) {
                        if (!activeUsers.contains(email)) {
                            toRemove.add(email);
                        }
                    }
                    for (String email : toRemove) {
                        markers.get(email).destroy();
                        markers.remove(email);
                    }
                } catch (ConnectionFailureException e) {}
                currentlySettingMarkers = false;
            }
        };
        markerUpdater.scheduleAtFixedRate(task, 500, 1000);

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        LocationSender.start();
    }

    public void onStart() {
        super.onStart();
        if (apiClient != null) {
            apiClient.connect();
        }
    }

    public void onStop() {
        super.onStop();
        if (apiClient != null) {
            apiClient.disconnect();
        }
    }

    public void onConnectionSuspended(int i) {
        Toast.makeText(MainMap.this, "Connection to API client suspended", Toast.LENGTH_SHORT).show();
    }

    public void onConnected(Bundle bundle) {
        Toast.makeText(MainMap.this, "Connected to API client", Toast.LENGTH_SHORT).show();

        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(750);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
        } catch (SecurityException e) {
            Toast.makeText(MainMap.this, "No Permission For Location Services.", Toast.LENGTH_LONG).show();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(MainMap.this, "Connection to API client failed.", Toast.LENGTH_SHORT).show();
    }

    boolean currentlySendingLogOut;
    private class AsyncLogout extends AsyncTask<Void, Void, Void> {
        private boolean success;

        protected void onPreExecute() {
            super.onPreExecute();
            success = false;
        }

        protected Void doInBackground(Void... params) {
            try {
                success = Server.logout().equals("OK");
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                Toast.makeText(MainMap.this, "Failed to logout.", Toast.LENGTH_SHORT).show();
                currentlySendingLogOut = false;
            } else {
                markerUpdater.cancel();

                UserMapMarkerUpdater.stop();
                UserInfoUpdater.stop();
                LocationSender.stop();
                SlideMenuUpdater.stop();
                UserInfoFactory.clear();
                UserInfoUpdater.clear();

                ActiveUser.clearSession();
                SessionLoader.clean();

                finish();
            }
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (supportMapFragment.isAdded())
            sFm.beginTransaction().hide(supportMapFragment).commit();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(MainMap.this, Settings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainMap.this, ProfileSettings.class);
            intent.putExtra("email", ActiveUser.getEmail());
            startActivity(intent);
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(MainMap.this, FeedbackSettings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_vehicle_type) {
            startActivity(new Intent(MainMap.this, CarInfoSettings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(MainMap.this, Help.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainMap.this, About.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_terms_conditions) {
            startActivity(new Intent(MainMap.this, TermsAndConditions.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_contact_info) {
            startActivity(new Intent(MainMap.this, ContactInfoSettings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_ride_preferences) {
            startActivity(new Intent(MainMap.this, RidePreferences.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        } else if (id == R.id.nav_logout) {
            if (!currentlySendingLogOut) {
                currentlySendingLogOut = true;
                new AsyncLogout().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void updateMapUiSettings() {
        googleMap.getUiSettings().setRotateGesturesEnabled(LocalUserPreferences.getRotateMap());
        googleMap.getUiSettings().setTiltGesturesEnabled(LocalUserPreferences.getTiltMap());
        googleMap.getUiSettings().setZoomGesturesEnabled(LocalUserPreferences.getZoomMap());
    }

    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Make map user interface settings change with
        // respect to the current map settings.
        updateMapUiSettings();
        LocalUserPreferences.registerOnChangeListener(new Runnable() {
            public void run() {
                updateMapUiSettings();
            }
        });

        // Destination Setting callback.
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng latLng) {
                if (currentlySettingDestination) {
                    String e = ActiveUser.getEmail();
                    if (destinationMarkers.containsKey(e)) {
                        destinationMarkers.get(e).destroy();
                        destinationMarkers.remove(e);
                    }
                    destinationMarkers.put(e, new UserMapDestinationMarker(googleMap, e, latLng));
                    final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                    selectDestination.startAnimation(slide_up);

                    currentlySettingDestination = false;
                    new AsyncSendDestination(ActiveUser.getInfo(), latLng.latitude, latLng.longitude).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        UserMapMarker.init(googleMap);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_page, menu);
        return true;
    }
}
