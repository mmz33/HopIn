package aub.hopin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.*;
import android.widget.AdapterView;
import android.app.Fragment;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class SwipeLeft extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private List<ItemSlideMenu> listSliding;
    private SlidingMenuAdapter adapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private RelativeLayout drawerPane;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.main_content);
        mapFragment.getMapAsync(this);
        setUpMap();

        listViewSliding = (ListView) findViewById(R.id.nav_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerPane = (RelativeLayout) findViewById(R.id.drawer_pane);
        listSliding = new ArrayList<>();


        listSliding.add(new ItemSlideMenu(R.drawable.ic_action_settings, "Settings"));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_action_about, "About"));

        adapter = new SlidingMenuAdapter(this, listSliding);

        listViewSliding.setAdapter(adapter);

        setTitle(listSliding.get(0).getTitle());
        listViewSliding.setItemChecked(0, true);
        drawerLayout.closeDrawer(drawerPane);

        //Display Map Fragment
        replaceFragment(1);

        listViewSliding.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setTitle(listSliding.get(position).getTitle());
                        listViewSliding.setItemChecked(position, true);
                        replaceFragment(position);
                        drawerLayout.closeDrawer(drawerPane);
                    }
                }
        );

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    public void replaceFragment(int pos) {
        Fragment fragment = null;
        switch (pos) {
            case 0:
                fragment = new SettingsFragment();
                startActivity(new Intent(SwipeLeft.this, Settings.class));
                break;
            default:
                fragment = new Fragment().getFragmentManager().findFragmentById(R.id.main_content);
                break;
        }
        if(fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.temp_page, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    //make the current location as default
    private void setUpMap() {
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {mMap.setMyLocationEnabled(true);} catch (SecurityException e) {}
        if(mMap != null) setUpMap();
        else             Log.e("map error", "Map is not initialized!");
    }
}
