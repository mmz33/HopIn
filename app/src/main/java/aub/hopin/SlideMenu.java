package aub.hopin;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SlideMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{

    private SupportMapFragment supportMapFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private GoogleMap mMap;
    private android.support.v4.app.FragmentManager sFm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //Show the map
        if (!supportMapFragment.isAdded())
            sFm.beginTransaction().add(R.id.content_frame, supportMapFragment).commit();

        sFm.beginTransaction().show(supportMapFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (supportMapFragment.isAdded())
            sFm.beginTransaction().hide(supportMapFragment).commit();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(SlideMenu.this, Settings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        }
        else if(id == R.id.nav_profile) {
            startActivity(new Intent(SlideMenu.this, ProfileSettings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        }
        else if(id == R.id.nav_schedule) {
            startActivity(new Intent(SlideMenu.this, ScheduleSettings.class));
            sFm.beginTransaction().show(supportMapFragment).commit();
        }
        else if(id == R.id.nav_feedback) {
            startActivity(new Intent(SlideMenu.this, FeedbackSettings.class));
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
        try {mMap.setMyLocationEnabled(true);} catch (SecurityException e) {}
        if(mMap != null) setUpMap();
        else             Log.e("map error", "Map is not initialized!");
    }

    //make the current location as default
    public void setUpMap() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = null;
        try {location = locationManager.getLastKnownLocation(provider);} catch (SecurityException e) {}
        if (location != null) {
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);
            this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
            mMap.addMarker(new MarkerOptions().position(target).title("You are here!").snippet("Consider yourself located"));
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
