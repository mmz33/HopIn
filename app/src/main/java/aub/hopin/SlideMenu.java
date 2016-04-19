package aub.hopin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SlideMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, Animation.AnimationListener, View.OnClickListener{

    private SupportMapFragment supportMapFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private GoogleMap mMap;
    private android.support.v4.app.FragmentManager sFm;

    private Button requestButton;

    private Animation animation1;
    private Animation animation2;
    private Animation animation3;
    private boolean isP = false;
    private boolean isO = false;
    private boolean isW = false;

    private ImageView img;


    private HashMap<String, UserInfo> userInfoMap;
    private HashMap<String, GroundOverlay> userMarkers;

    private LocationManager locationManager;
    private String provider;

    // Gets the last known location using
    // the location services of the device.
    private Location getCurrentLocation() {
        try {
            return locationManager.getLastKnownLocation(provider);
        } catch (SecurityException e) {
            Log.e("error", "faced security exception when querying location.");
            return null;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation == animation1) {
            if(isP) {
                ((ImageView)findViewById(R.id.slide_menu_notify_image)).setImageResource(R.drawable.p_button);
            } else if(isO) {
                ((ImageView)findViewById(R.id.slide_menu_notify_image)).setImageResource(R.drawable.o_button);
            } else if(isW) {
                ((ImageView)findViewById(R.id.slide_menu_notify_image)).setImageResource(R.drawable.w_button);
            }
        } else {

        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onClick(View v) {

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
                if (Server.sendGlobalPosition(email, location.getLongitude(), location.getLatitude()).equals("OK")) {
                    //Log.i("error", "positioning info successfully sent.");
                } else {
                    Log.e("error", "failed to send positioning info.");
                }
            } catch (ConnectionFailureException e) {
                // TODO
                // handle this exception somehow?
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private HashMap<String, GroundOverlayOptions> asyncToCreate = new HashMap<>();
    private HashMap<String, GroundOverlayOptions> asyncToUpdate = new HashMap<>();

    // Asynchronous marker setup. This will update the
    // markers on the map according to the locations
    // of the active users as specified by the server.
    private class AsyncSetupMarkers extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Void doInBackground(Void... params) {
            try {
                HashMap<String, String> activeUserPositions = Server.queryActiveUsersAndPositions();
                if (activeUserPositions != null) {
                    Handler handler = new Handler(getApplicationContext().getMainLooper());
                    //Log.i("error", "successfully queried active users.");

                    asyncToCreate.clear();
                    asyncToUpdate.clear();

                    for (String email : activeUserPositions.keySet()) {
                        if (!userInfoMap.containsKey(email))
                            userInfoMap.put(email, new UserInfo(email, false));
                        UserInfo uInfo = userInfoMap.get(email);
                        String coords[] = activeUserPositions.get(email).split(" ");
                        LatLng target = new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                        if (userMarkers.containsKey(email)) {
                            GroundOverlayOptions options = new GroundOverlayOptions()
                                    .position(target, 100f, 100f);
                            asyncToUpdate.put(email, options);
                        } else {
                            GroundOverlayOptions options = new GroundOverlayOptions()
                                    .image(BitmapDescriptorFactory.fromBitmap(uInfo.profileImage))
                                    .position(target, 100f, 100f);
                            asyncToCreate.put(email, options);
                        }
                    }
                    Runnable markerUpdater = new Runnable() {
                        public void run() {
                            for (String email : asyncToCreate.keySet()) {
                                userMarkers.put(email, mMap.addGroundOverlay(asyncToCreate.get(email)));
                            }
                            for (String email : asyncToUpdate.keySet()) {
                                GroundOverlayOptions options = asyncToUpdate.get(email);
                                userMarkers.get(email).setPosition(options.getLocation());
                            }
                        }
                    };
                    handler.post(markerUpdater);
                } else {
                    Log.e("error", "failed to query active users.");
                }
            } catch (ConnectionFailureException e) {
                // TODO
                // handle this exception somehow?
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class AsyncSendNotification extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        private UserState state;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            info = ActiveUser.getInfo();
            if (info.mode == UserMode.PassengerMode) {
                state = UserState.Wanting;
            } else {
                state = UserState.Offering;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (Server.sendStateSwitch(info.email, state).equals("OK")) {
                    info.state = state;
                    Log.i("error", "Successfully sent notify message!");
                } else {
                    Log.e("error", "Something went wrong with sending notify message");
                }
            } catch (ConnectionFailureException e) {}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //requestButton = (Button) findViewById(R.id.slide_menu_button);

        UserInfo info = ActiveUser.getInfo();

        userMarkers = new HashMap<>();
        userInfoMap = new HashMap<>();

        userInfoMap.put(ActiveUser.getEmail(), ActiveUser.getInfo());

        /*if (info.mode == UserMode.PassengerMode) requestButton.setText("Give me a ride");
        else if (info.mode == UserMode.DriverMode) requestButton.setText("I am offering a ride");

        ModeSwitchEvent.register(new ModeSwitchListener() {
            @Override
            public void onSwitch(String email) {
                UserInfo userInfo = ActiveUser.getActiveUserInfo();
                if (email.equals(userInfo.email)) {
                    if (userInfo.mode == UserMode.PassengerMode)
                        requestButton.setText("Give me a ride");
                    else if (userInfo.mode == UserMode.DriverMode)
                        requestButton.setText("I am offering a ride");
                }
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncSendNotification().execute();
            }
        });

        switch (info.state) {
            case Passive:
                isP = true;
                isO = false;
                isW = false;
                break;
            case Offering:
                isP = false;
                isO = true;
                isW = false;
                break;
            case Wanting:
                isP = false;
                isO = false;
                isW = true;
                break;
            default:
                isP = false;
                isO = false;
                isW = false;
                break;
        }*/

        img = (ImageView) findViewById(R.id.slide_menu_notify_image);
        if (isP) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.p_button);
            bitmap = ImageUtils.makeRounded(bitmap);
            img.setImageBitmap(bitmap);
        } else if (isW) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w_button);
             bitmap = ImageUtils.makeRounded(bitmap);
            img.setImageBitmap(bitmap);
        } else{
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_button);
            bitmap = ImageUtils.makeRounded(bitmap);
            img.setImageBitmap(bitmap);
        }

        animation1 = AnimationUtils.loadAnimation(this, R.anim.first);
        animation1.setAnimationListener(this);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.second);
        animation2.setAnimationListener(this);

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

        // Show the map
        if (!supportMapFragment.isAdded())
            sFm.beginTransaction().add(R.id.content_frame, supportMapFragment).commit();
        sFm.beginTransaction().show(supportMapFragment).commit();

        // Get location services.
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);

        //try {
        //    locationManager.requestLocationUpdates(100, 1, new Criteria(), null);
        //} catch (SecurityException e) {}

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Location location = getCurrentLocation();

                if (location != null)
                    new AsyncSendPosition(location).execute();
                else
                    Log.e("error", "failed to get current location in periodic task.");

                new AsyncSetupMarkers().execute();

                //runOnUiThread(new Runnable() {
                    //public void run() {
                        //Location location = getCurrentLocation();
                        //if (location != null) {
                            //LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                            //CameraPosition.Builder builder = new CameraPosition.Builder();
                            //builder.zoom(15);
                            //builder.target(target);
                            //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                        //}
                    //}
                //});
            }
        };
        t.scheduleAtFixedRate(task, 1000, 1000);
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
            startActivity(new Intent(SlideMenu.this, ProfileSettings.class));
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
        } else {
            super.onBackPressed();
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
