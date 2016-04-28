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
import android.view.Gravity;
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

    // The user markers that appear on the map.
    private HashMap<String, UserMapMarker> markers = new HashMap<>();
    private Semaphore markersSemaphore = new Semaphore(1);

    // The destination markers that appear on the map.
    private HashMap<String, UserMapDestinationMarker> destinationMarkers = new HashMap<>();
    private Semaphore destinationMarkersSemaphore = new Semaphore(1);

    private boolean currentlySettingDestination = false;
    private boolean currentlySettingMarkers = false;

    public boolean isShowingDestinationSetter() {
        return currentlySettingDestination;
    }

    private ImageView slideMenuProfileImage;
    private TextView slideMenuUserName;
    private TextView slideMenuUserEmail;
    private Button userModeButton;
    private Button userStateButton;
    private LinearLayout selectDestination;

    private LinearLayout groundoverlay;
    private Button profileButton;
    private Button requestOrOfferButton;
    public LinearLayout getProfileBar() {
        return groundoverlay;
    }
    public Button getProfileBarProfileButton() {
        return profileButton;
    }
    public Button getProfileBarRequestButton() {
        return requestOrOfferButton;
    }

    // Sets the one and only email of the active destination marker that
    // the user can see at the current time besides his own.
    private String showingDestinationMarkerFor = "";
    public void setShowingDestinationMarker(String email) {
        showingDestinationMarkerFor = email;
        updateUI();
    }

    // This will run whenever the application gets a location update
    // from the google service.
    private boolean zoomedInYet = false;
    public void onLocationChanged(Location loc) {
        if (loc == null) return;

        LocationSender.setCurrentLocation(loc);

        if (!zoomedInYet && googleMap != null) {
            zoomedInYet = true;
            LatLng target = new LatLng(loc.getLatitude(), loc.getLongitude());
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(17);
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

        public AsyncSendDestination(UserInfo info, double latitude, double longitude) {
            this.info = info;
            success = false;
            lat = latitude;
            lon = longitude;
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
            } else {
                info.curDestinationLatitude = lat;
                info.curDestinationLongitude = lon;
            }
            updateUI();
        }
    }

    // Slides down the 'Hold Destination' bar.
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
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    // Slides up the 'Hold Destination' bar.
    private void slideUpSelectDestination() {
        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        selectDestination.startAnimation(slide_up);
        slide_up.setFillAfter(true);
        currentlySettingDestination = false;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    // Updates the User interface.
    private void updateUI() {
        UserInfo info = ActiveUser.getInfo();
        if (info == null) return;

        // Refresh the user interface according to the
        // user information mode.
        switch (info.mode) {
            case DriverMode:
                userStateButton.setText("Passive");
                userStateButton.setBackgroundResource(R.color.colorGrey);
                userModeButton.setText("Driver");
                userModeButton.setBackgroundResource(R.color.colorOrange);
                break;
            case PassengerMode:
                userStateButton.setText("Passive");
                userStateButton.setBackgroundResource(R.color.colorGrey);
                userModeButton.setText("Passenger");
                userModeButton.setBackgroundResource(R.color.colorBlue);
                break;
        }

        // Refresh the user interface according to the
        // user information state.
        switch (info.state) {
            case Passive:
                userStateButton.setText("Passive");
                userStateButton.setBackgroundResource(R.color.colorGrey);
                break;
            case Offering:
                userStateButton.setText("Offering");
                userStateButton.setBackgroundResource(R.color.colorGreen);
                break;
            case Wanting:
                userStateButton.setText("Wanting");
                userStateButton.setBackgroundResource(R.color.colorRed);
                break;
        }

        // Setup the slide menu information.
        slideMenuProfileImage.setImageBitmap(info.getProfileImage());
        slideMenuUserName.setText(info.firstName + " " + info.lastName);
        slideMenuUserEmail.setText(info.email);

        // Setup the visibility of all the destination markers.
        try { destinationMarkersSemaphore.acquire(); } catch (InterruptedException e) {}
        for (String email : destinationMarkers.keySet()) {
            UserMapDestinationMarker marker = destinationMarkers.get(email);
            if (marker == null) continue;

            if (email.equals(showingDestinationMarkerFor) || email.equals(info.email)) {
                marker.showMarker();
            } else {
                marker.hideMarker();
            }
        }
        destinationMarkersSemaphore.release();
    }

    private boolean sendingStateModeChange = false;

    // Sends a mode change to the server.
    private class AsyncStateModeChange extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        private boolean success;
        private UserMode mode;
        private UserState state;

        public AsyncStateModeChange(UserMode mode, UserState state) {
            this.info = ActiveUser.getInfo();
            this.mode = mode;
            this.state = state;
            this.success = false;
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
            } else {
                info.mode = mode;
                info.state = state;
                boolean needsDest = (mode == UserMode.PassengerMode && state == UserState.Wanting);
                needsDest = needsDest || (mode == UserMode.DriverMode && state == UserState.Offering);
                if (needsDest) {
                    dropDownSelectDestination();
                }
                updateUI();
            }
            sendingStateModeChange = false;
        }
    }

    protected void setupSlideMenu() {
        View hView = navigationView.getHeaderView(0);
        slideMenuProfileImage = (ImageView)hView.findViewById(R.id.nav_header_image_view);
        slideMenuUserName = (TextView)hView.findViewById(R.id.nav_header_user_name);
        slideMenuUserEmail = (TextView)hView.findViewById(R.id.nav_header_user_email);

        assert slideMenuProfileImage != null;
        assert slideMenuUserEmail != null;
        assert slideMenuUserName != null;

        SlideMenuUpdater.start(slideMenuProfileImage);

        userModeButton = (Button)hView.findViewById(R.id.nav_header_main_button1);
        userStateButton = (Button)hView.findViewById(R.id.nav_header_main_button2);

        userModeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (sendingStateModeChange) return;
                sendingStateModeChange = true;

                UserInfo info = ActiveUser.getInfo();
                switch (info.mode) {
                    case PassengerMode:
                        new AsyncStateModeChange(UserMode.DriverMode, UserState.Passive).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    case DriverMode:
                        new AsyncStateModeChange(UserMode.PassengerMode, UserState.Passive).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                }
            }
        });

        userStateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (sendingStateModeChange) return;
                sendingStateModeChange = true;

                UserInfo info = ActiveUser.getInfo();
                switch (info.mode) {
                    case PassengerMode:
                        switch (info.state) {
                            case Wanting:
                                //if (destinationMarkers.containsKey(info.email)) {
                                //    UserMapDestinationMarker destinationMarker = destinationMarkers.get(info.email);
                                //    destinationMarker.destroy();
                                //    destinationMarkers.remove(info.email);
                                //}
                                new AsyncStateModeChange(UserMode.PassengerMode, UserState.Passive).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                            case Passive:
                                new AsyncStateModeChange(UserMode.PassengerMode, UserState.Wanting).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                        }
                        break;
                    case DriverMode:
                        switch (info.state) {
                            case Offering:
                                //if (destinationMarkers.containsKey(info.email)) {
                                //    UserMapDestinationMarker destinationMarker = destinationMarkers.get(info.email);
                                //    destinationMarker.destroy();
                                //    destinationMarkers.remove(info.email);
                                //}
                                new AsyncStateModeChange(UserMode.DriverMode, UserState.Passive).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                            case Passive:
                                new AsyncStateModeChange(UserMode.DriverMode, UserState.Offering).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                break;
                        }
                        break;
                }
            }
        });

        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        groundoverlay = (LinearLayout)findViewById(R.id.groundoverlay_layout);
        groundoverlay.setVisibility(LinearLayout.GONE);
        groundoverlay.setGravity(Gravity.BOTTOM);
        profileButton = (Button)findViewById(R.id.bt_orders);
        requestOrOfferButton = (Button)findViewById(R.id.bt_credit);

        setupSlideMenu();

        // Show the map
        if (!supportMapFragment.isAdded()) {
            sFm.beginTransaction().add(R.id.content_frame, supportMapFragment).commit();
        }
        sFm.beginTransaction().show(supportMapFragment).commit();

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
                        UserInfo info = UserInfoFactory.get(email, true);
                        if (info == null) continue;

                        try { markersSemaphore.acquire(); } catch (InterruptedException e) {}
                        if (!markers.containsKey(email)) {
                            final String e = email;
                            MainMap.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (markers != null && googleMap != null) {
                                        markers.put(e, new UserMapMarker(googleMap, e));
                                    }
                                }
                            });
                        }
                        markersSemaphore.release();

                        try { destinationMarkersSemaphore.acquire(); } catch (InterruptedException e) {}
                        if (!destinationMarkers.containsKey(email) && info.hasDestination) {
                            final String e = email;
                            MainMap.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (destinationMarkers != null && googleMap != null) {
                                        UserInfo info = UserInfoFactory.get(e);
                                        UserMapDestinationMarker marker = new UserMapDestinationMarker(googleMap, e, new LatLng(info.curDestinationLatitude, info.curDestinationLongitude));
                                        destinationMarkers.put(e, marker);
                                        marker.hideMarker();
                                    }
                                }
                            });
                        }
                        destinationMarkersSemaphore.release();
                    }

                    new Handler(Looper.getMainLooper()).post(
                            new Runnable() {
                                public void run() {
                                    updateUI();
                                }
                            });

                    // Remove the markers for the users who are no longer of interest.
                    try { markersSemaphore.acquire();
                          destinationMarkersSemaphore.acquire(); } catch (InterruptedException e) {}
                    ArrayList<String> toRemoveMarkers = new ArrayList<>();
                    ArrayList<String> toRemoveDestMarkers = new ArrayList<>();
                    for (String email : markers.keySet()) {
                        if (!activeUsers.contains(email)) {
                            toRemoveMarkers.add(email);
                        }
                    }
                    for (String email : destinationMarkers.keySet()) {
                        UserInfo info = UserInfoFactory.get(email);
                        if (info == null) continue;
                        UserMapDestinationMarker marker = destinationMarkers.get(email);
                        if (marker == null) continue;

                        double deltaLat = Math.abs(info.curDestinationLatitude - marker.getLatitude());
                        double deltaLon = Math.abs(info.curDestinationLongitude - marker.getLongitude());
                        boolean changed = (deltaLat + deltaLon > 0.0000001);
                        if (!activeUsers.contains(email) || !info.hasDestination || changed) {
                            toRemoveDestMarkers.add(email);
                        }
                    }
                    for (String email : toRemoveMarkers) {
                        UserMapMarker marker = markers.get(email);
                        if (marker == null) continue;
                        marker.destroy();
                        markers.remove(email);
                    }
                    for (String email : toRemoveDestMarkers) {
                        UserMapDestinationMarker marker = destinationMarkers.get(email);
                        if (marker == null) continue;
                        marker.destroy();
                        destinationMarkers.remove(email);
                    }
                    markersSemaphore.release();
                    destinationMarkersSemaphore.release();
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
        MessagesHandler.start();
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

    public void onConnected(Bundle bundle) {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(500);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
        } catch (SecurityException e) {
            Toast.makeText(MainMap.this, "No Permission For Location Services.", Toast.LENGTH_LONG).show();
        }
    }

    // These will not be implemented.
    public void onConnectionSuspended(int i) {}
    public void onConnectionFailed(ConnectionResult result) {}

    private class AsyncLogout extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Void doInBackground(Void... params) {
            try {
                Server.logout();
            } catch (ConnectionFailureException e) {}
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (supportMapFragment.isAdded())
            sFm.beginTransaction().hide(supportMapFragment).commit();

        switch (id) {
            case R.id.nav_settings:
                startActivity(new Intent(MainMap.this, Settings.class));
                sFm.beginTransaction().show(supportMapFragment).commit();
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(MainMap.this, ProfileSettings.class);
                intent.putExtra("email", ActiveUser.getEmail());
                startActivity(intent);
                sFm.beginTransaction().show(supportMapFragment).commit();
                break;
            case R.id.nav_feedback:
                startActivity(new Intent(MainMap.this, FeedbackSettings.class));
                sFm.beginTransaction().show(supportMapFragment).commit();
                break;
            case R.id.nav_vehicle_type:
                startActivity(new Intent(MainMap.this, CarInfoSettings.class));
                sFm.beginTransaction().show(supportMapFragment).commit();
                break;
            case R.id.nav_help:
                startActivity(new Intent(MainMap.this, Help.class));
                sFm.beginTransaction().show(supportMapFragment).commit();
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainMap.this, About.class));
                sFm.beginTransaction().show(supportMapFragment).commit();
                break;
            case R.id.nav_terms_conditions:
                startActivity(new Intent(MainMap.this, TermsAndConditions.class));
                sFm.beginTransaction().show(supportMapFragment).commit();
                break;
            case R.id.nav_contact_info:
                startActivity(new Intent(MainMap.this, ContactInfoSettings.class));
                sFm.beginTransaction().show(supportMapFragment).commit();
                break;
            case R.id.nav_ride_preferences:
                startActivity(new Intent(MainMap.this, RidePreferences.class));
                sFm.beginTransaction().show(supportMapFragment).commit();
                break;
            case R.id.nav_logout:
                if (markerUpdater != null) markerUpdater.cancel();

                MessagesHandler.stop();
                UserMapMarkerUpdater.stop();
                UserInfoUpdater.stop();
                LocationSender.stop();
                SlideMenuUpdater.stop();
                UserInfoFactory.clear();
                UserInfoUpdater.clear();
                SessionLoader.clean();

                new AsyncLogout().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                finish();
                break;
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

    // Updates the available map gestures.
    public void updateMapUiSettings() {
        googleMap.getUiSettings().setRotateGesturesEnabled(LocalUserPreferences.getRotateMap());
        googleMap.getUiSettings().setTiltGesturesEnabled(LocalUserPreferences.getTiltMap());
        googleMap.getUiSettings().setZoomGesturesEnabled(LocalUserPreferences.getZoomMap());
    }

    // This will execute when the map is ready.
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
                    slideUpSelectDestination();
                    new AsyncSendDestination(ActiveUser.getInfo(), latLng.latitude, latLng.longitude).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        UserMapMarker.init(googleMap, this);
        UserMapMarkerUpdater.start();
        UserInfoUpdater.start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_page, menu);
        return true;
    }
}
