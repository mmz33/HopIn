package aub.hopin;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class UserMapMarker {
    private static HashMap<String, UserMapMarker> overlays = new HashMap<>();

    private GoogleMap parentMap;
    private GroundOverlay overlay;
    private LatLng location;
    private UserInfo userInfo;

    public UserMapMarker(GoogleMap map, String email) {
        if (map == null || email == null || email.length() == 0) throw new IllegalArgumentException();

        this.parentMap = map;
        this.userInfo = UserInfoFactory.get(email);
        this.location = new LatLng(userInfo.latitude, userInfo.longitude);

        GroundOverlayOptions options = new GroundOverlayOptions()
                .position(this.location, 100f, 100f)
                .image(getImageDescripter());

        this.overlay = parentMap.addGroundOverlay(options);
        this.overlays.put(overlay.getId(), this);
        this.overlay.setClickable(true);

        UserMapMarkerUpdater.requestPeriodicUpdates(this);

        // The user himself should always be drawn over anyone standing very close to him.
        if (email.equals(ActiveUser.getEmail())) {
            overlay.setZIndex(1.0f);
        }
        overlays.put(this.overlay.getId(), this);
    }

    public static void init(GoogleMap map, final LinearLayout groundov, final Animation slide_up, Animation slide_down) {
        if (map == null) return;

        map.setOnGroundOverlayClickListener(new GoogleMap.OnGroundOverlayClickListener() {
            public void onGroundOverlayClick(GroundOverlay groundOverlay) {
                Toast.makeText(GlobalContext.get(), "Overlay Clicked!", Toast.LENGTH_SHORT).show();
                UserMapMarker marker = overlays.get(groundOverlay.getId());

                groundov.startAnimation(slide_up);

            }
        });
    }

    private String ringColor() {
        switch (userInfo.state) {
            case Passive:  return "#A8A8A8";
            case Offering: return "#58CF58";
            case Wanting:  return "#FF4C4C";
        }
        throw new IllegalArgumentException();
    }

    private BitmapDescriptor getImageDescripter() {
        Bitmap bmp = ImageUtils.overlayRoundBorder(userInfo.getProfileImage(), ringColor());
        return BitmapDescriptorFactory.fromBitmap(bmp);
    }

    public double getLatitude() {
        if (location != null) return location.latitude;
        return 0.0;
    }

    public double getLongitude() {
        if (location != null) return location.longitude;
        return 0.0;
    }

    public void setPosition(final double latitude, final double longitude) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                UserMapMarker.this.location = new LatLng(latitude, longitude);
                if (overlay != null) overlay.setPosition(UserMapMarker.this.location);
            }
        });
    }

    public String getEmail() {
        if (this.userInfo == null) return "";
        if (this.userInfo.email == null) return "";

        return this.userInfo.email;
    }

    public void updateImage() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() { if (overlay != null) overlay.setImage(getImageDescripter()); }
        });
    }

    public void destroy() {
        UserMapMarkerUpdater.remove(this);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (overlay != null) overlay.remove();
            }
        });
    }
}
