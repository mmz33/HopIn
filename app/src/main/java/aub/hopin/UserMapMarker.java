package aub.hopin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
    private static MainMap mainAct = null;

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
                .position(this.location, 80f, 80f)
                .image(getImageDescripter());

        this.overlay = parentMap.addGroundOverlay(options);
        this.overlay.setClickable(true);

        UserMapMarkerUpdater.requestPeriodicUpdates(this);

        // The user himself should always be drawn over anyone standing very close to him.
        if (email.equals(ActiveUser.getEmail())) {
            overlay.setZIndex(1.0f);
        }
        overlays.put(this.overlay.getId(), this);
    }

    private static boolean isSomethingCurrentlyClicked = false;
    private static UserMapMarker currentlyClicked = null;
    private static UserInfo currentlyConsidered = null;

    private static class AsyncSendMessage extends AsyncTask<Void, Void, Void> {
        private UserInfo info;
        private String recipient;
        private String message;
        private boolean success;

        public AsyncSendMessage(String recip, String mes) {
            this.info = ActiveUser.getInfo();
            this.recipient = recip;
            this.message = mes;
            this.success = false;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                success = Server.leaveMessage(info.email, recipient, message).equals("OK");
            } catch (ConnectionFailureException e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success) {
                Toast.makeText(mainAct, "Sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mainAct, "Failed to send!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void init(GoogleMap map, final MainMap mainActivity) {
        if (map == null) return;

        mainAct = mainActivity;

        map.setOnGroundOverlayClickListener(new GoogleMap.OnGroundOverlayClickListener() {
            public void onGroundOverlayClick(GroundOverlay groundOverlay) {
                UserMapMarker marker = overlays.get(groundOverlay.getId());
                String email = marker.getEmail();
                if (email == null) return;
                if (email == "") return;

                UserInfo info = UserInfoFactory.get(email);
                if (info == null) return;

                if (email.equals(ActiveUser.getEmail())) return;

                //Toast.makeText(GlobalContext.get(), "Clicked!", Toast.LENGTH_SHORT).show();
                Animation slide_up = AnimationUtils.loadAnimation(mainAct, R.anim.slide_up);
                Animation slide_down = AnimationUtils.loadAnimation(mainAct, R.anim.slide_down);
                slide_up.setFillAfter(true);
                slide_down.setFillAfter(true);

                //if (isSomethingCurrentlyClicked) {
                mainAct.getProfileBar().startAnimation(slide_up);
                mainAct.getProfileBar().setVisibility(LinearLayout.VISIBLE);
                mainAct.getProfileBarProfileButton().setText(info.firstName + "'s Profile");

                mainAct.setShowingDestinationMarker(email);

                //mainAct.showDestinationMarker(email);

                UserInfo me = ActiveUser.getInfo();
                if (me == null) return;

                if (me.mode == UserMode.DriverMode && info.mode == UserMode.PassengerMode) {
                    mainAct.getProfileBarRequestButton().setText("Offer Ride!");
                    mainAct.getProfileBarRequestButton().setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Animation slide_down = AnimationUtils.loadAnimation(mainAct, R.anim.slide_down);
                            slide_down.setFillAfter(true);
                            mainAct.getProfileBar().startAnimation(slide_down);
                            new AsyncSendMessage(currentlyConsidered.email, "OFFER").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                } else if (me.mode == UserMode.PassengerMode && info.mode == UserMode.DriverMode) {
                    mainAct.getProfileBarRequestButton().setText("Request Ride!");
                    mainAct.getProfileBarRequestButton().setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Animation slide_down = AnimationUtils.loadAnimation(mainAct, R.anim.slide_down);
                            slide_down.setFillAfter(true);
                            mainAct.getProfileBar().startAnimation(slide_down);
                            new AsyncSendMessage(currentlyConsidered.email, "REQUEST").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                } else {
                    mainAct.getProfileBarRequestButton().setText("");
                    mainAct.getProfileBarRequestButton().setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {}
                    });
                }

                mainAct.getProfileBarProfileButton().setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Animation slide_down = AnimationUtils.loadAnimation(mainAct, R.anim.slide_down);
                        slide_down.setFillAfter(true);

                        mainAct.getProfileBar().startAnimation(slide_down);
                        Intent intent = new Intent(mainAct, ProfileSettings.class);
                        intent.putExtra("email", currentlyConsidered.email);
                        mainAct.startActivity(intent);
                    }
                });

                // Update info
                isSomethingCurrentlyClicked = true;
                currentlyClicked = marker;
                currentlyConsidered = info;
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
