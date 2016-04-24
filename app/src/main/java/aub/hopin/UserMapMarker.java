package aub.hopin;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

public class UserMapMarker {
    private GoogleMap parentMap;
    private GroundOverlay overlay;
    private LatLng location;
    private UserInfo userInfo;

    public UserMapMarker(GoogleMap map, String email) {
        this.userInfo = UserInfoFactory.get(email);
        this.parentMap = map;
        this.location = new LatLng(userInfo.latitude, userInfo.longitude);

        GroundOverlayOptions options = new GroundOverlayOptions()
                .position(this.location, 100f, 100f)
                .image(getImageDescripter());

        overlay = parentMap.addGroundOverlay(options);
        UserMapMarkerUpdater.requestPeriodicUpdates(this);
    }

    private String ringColor() {
        switch (userInfo.state) {
            case Passive:  return "#FFFFFF";
            case Offering: return "#00E500";
            case Wanting:  return "#E50000";
        }
        throw new IllegalArgumentException();
    }

    private BitmapDescriptor getImageDescripter() {
        Bitmap bmp = ImageUtils.overlayRoundBorder(userInfo.getProfileImage(), ringColor());
        return BitmapDescriptorFactory.fromBitmap(bmp);
    }

    public String getEmail() {
        return this.userInfo.email;
    }

    public void updateImage() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (overlay != null) {
                    overlay.setImage(getImageDescripter());
                }
            }
        });
    }
    public void destroy() {
        UserMapMarkerUpdater.remove(this);
        overlay.remove();
    }
}
