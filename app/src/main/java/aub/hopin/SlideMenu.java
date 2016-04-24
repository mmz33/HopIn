package aub.hopin;


import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
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



public class SlideMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private SupportMapFragment supportMapFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private GoogleMap googleMap;
    private FragmentManager sFm;

    private HashMap<String, UserMapMarker> markers;
    private HashMap<String, UserMapDestinationMarker> destinationMarkers;
    private boolean currentlySettingDestination;

    // Asynchronous marker setup. This will update the
    // markers on the map according to the locations
    // of the active users as specified by the server.
    private class AsyncSetupMarkers extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                ArrayList<String> activeUsers = Server.queryActiveUsers();

                for (String email : activeUsers) {
                    UserInfo info = UserInfoFactory.get(email);
                    if (info == null) continue;
                    if (!info.isValid()) continue;

                    if (!markers.containsKey(email)) {
                        final String e = email;
                        SlideMenu.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                markers.put(e, new UserMapMarker(googleMap, e));
                            }
                        });
                    }
                }

                ArrayList<String> toRemove = new ArrayList<String>();
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
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class AsyncSendDestination extends AsyncTask<Void, Void, Void> {
        private boolean success;
        private String email;
        private double lat;
        private double lon;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //try {
                //if(sendDestination(email, lat, lon).equals("OK")) {
                  //  success = true;
                //}
            //} catch (ConnectionFailureException e) {}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!success)
                Toast.makeText(SlideMenu.this, "Failed to send destination!", Toast.LENGTH_SHORT).show();
        }
    }

    private ImageView imageView;
    private TextView userNameTextView;
    private TextView userEmailTextView;

    private Button button1;
    private Button button2;

    private LinearLayout selectDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);

        setContentView(R.layout.activity_main_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        markers = new HashMap<>();
        destinationMarkers = new HashMap<>();

        supportMapFragment = SupportMapFragment.newInstance();
        supportMapFragment.getMapAsync(this);

        selectDestination = (LinearLayout)findViewById(R.id.select_destination_layout);
        selectDestination.setVisibility(LinearLayout.GONE);
        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        sFm = getSupportFragmentManager();

        currentlySettingDestination = false;

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
                if (button1.getText().toString().equals("Passenger")) {
                    if (button2.getText().equals("Wanting")) {
                        button2.setText("Passive");
                        button2.setBackgroundResource(R.color.colorGrey);
                    } else if (button2.getText().toString().equals("Passive")) {
                        button2.setText("Wanting");
                        button2.setBackgroundResource(R.color.colorRed);

                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        selectDestination.setVisibility(LinearLayout.VISIBLE);
                        selectDestination.startAnimation(slide_down);
                        slide_down.setFillAfter(true);
                        currentlySettingDestination = true;

                        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                            @Override
                            public void onMapLongClick(LatLng latLng) {
                                if (currentlySettingDestination) {
                                    String e = ActiveUser.getEmail();
                                    if (destinationMarkers.containsKey(e)) {
                                        destinationMarkers.get(e).destroy();
                                        destinationMarkers.remove(e);
                                    }
                                    destinationMarkers.put(e, new UserMapDestinationMarker(googleMap, e, latLng));
                                    selectDestination.startAnimation(slide_up);
                                    slide_up.setFillAfter(true);
                                    currentlySettingDestination = false;
                                }
                            }
                        });
                    }
                } else if (button1.getText().toString().equals("Driver")) {
                    if (button2.getText().toString().equals("Passive")) {
                        button2.setText("Offering");
                        button2.setBackgroundResource(R.color.colorGreen);
                    } else if (button2.getText().toString().equals("Offering")) {
                        button2.setText("Passive");
                        button2.setBackgroundResource(R.color.colorGrey);
                    }
                }
            }
        });

        UserInfo info = ActiveUser.getInfo();
        imageView.setImageBitmap(info.getProfileImage());
        userNameTextView.setText(info.firstName + " " + info.lastName);
        userEmailTextView.setText(info.email);

        // Show the map
        if (!supportMapFragment.isAdded()) {
            sFm.beginTransaction().add(R.id.content_frame, supportMapFragment).commit();
        }
        sFm.beginTransaction().show(supportMapFragment).commit();

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            public void run() { new AsyncSetupMarkers().execute(); }
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
                success = Server.logout().equals("OK");
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!success) {
                Toast.makeText(getApplicationContext(), "Failed to logout.", Toast.LENGTH_SHORT).show();
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

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void onMapReady(GoogleMap map) {
        LocationServices.start(getApplicationContext());
        googleMap = map;
        setUpMap();
    }

    // make the current location as default
    public void setUpMap() {
        Location location = LocationServices.getCurrentLocation();
        if (location != null) {
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_page, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
