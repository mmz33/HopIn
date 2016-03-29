package aub.hopin;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {mMap.setMyLocationEnabled(true);} catch (SecurityException e) {}
        if(mMap != null) setUpMap();
        else             Log.e("map error", "Map is not initialized!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
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
}
