package aub.hopin;


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
        this.parentMap = map;
        this.latLng = lt;
        this.userInfo = UserInfoFactory.get(email);
        marker = parentMap.addMarker(new MarkerOptions().position(this.latLng));
    }

    public void showMarker() {
        marker.setVisible(true);
    }

    public void hideMarker() {
        marker.setVisible(false);
    }

    public void destroy() {
        marker.remove();;
    }
}
