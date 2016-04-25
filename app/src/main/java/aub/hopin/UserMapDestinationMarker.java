package aub.hopin;


import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserMapDestinationMarker {
    private GoogleMap parentMap;
    private LatLng latLng;
    private Marker marker;
    private UserInfo userInfo;

    public UserMapDestinationMarker(GoogleMap map, String email, LatLng lt) {
        if (map == null || email == null || lt == null || email.length() == 0) throw new IllegalArgumentException();

        this.latLng = lt;
        this.parentMap = map;
        this.userInfo = UserInfoFactory.get(email);
        this.marker = parentMap.addMarker(new MarkerOptions().position(this.latLng));
    }

    public void showMarker() {
        if (marker != null) marker.setVisible(true);
    }

    public void hideMarker() {
        if (marker != null) marker.setVisible(false);
    }

    public double getLatitude() {
        return latLng.latitude;
    }

    public double getLongitude() {
        return latLng.longitude;
    }

    public String getEmail() {
        return this.userInfo.email;
    }

    public void destroy() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (marker != null) marker.remove();
            }
        });
    }
}