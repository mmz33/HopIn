package aub.hopin;

import android.util.Log;
import java.util.HashMap;

public class UserInfoLoader implements Runnable {
    private UserInfo info;

    public UserInfoLoader(UserInfo info) {
        this.info = info;
    }

    public void run() {
        if (info.email.length() == 0) {
            return;
        } else {
            try {
                HashMap<String, String> response = Server.queryUserInfo(info.email);
                updateFromServerResponse(info, response);
                if (info.onLoadCallback != null) {
                    info.onLoadCallback.run();
                }
                info.validate();
                UserInfoUpdater.requestPeriodicUpdates(info);
            } catch (ConnectionFailureException e) {}
        }
    }

    public static void updateFromServerResponse(UserInfo info, HashMap<String, String> response) {
        info.lock();

        try {
            info.firstName = response.get("firstname");
            info.lastName = response.get("lastname");
            info.email = response.get("email");
            info.age = Integer.parseInt(response.get("age"));
            info.gender = UserGender.fromSymbol(response.get("gender"));
            info.mode = UserMode.fromSymbol(response.get("mode"));
            info.state = UserState.fromSymbol(response.get("state"));
            info.role = UserRole.fromSymbol(response.get("role"));
            info.phoneNumber = response.get("phone");
            info.status = response.get("status");
            info.address = response.get("address");
            info.poBox = response.get("pobox");
            info.showingAddress = response.get("showaddress").equals("1");
            info.showingPhone = response.get("showphone").equals("1");
            info.active = response.get("active").equals("1");

            info.latitude = Double.parseDouble(response.get("latitude"));
            info.longitude = Double.parseDouble(response.get("longitude"));

            info.curDestinationLatitude = Double.parseDouble(response.get("curdestination_latitude"));
            info.curDestinationLongitude = Double.parseDouble(response.get("curdestination_longitude"));

            info.prefsWithMen = response.get("prefs_withmen").equals("1");
            info.prefsWithWomen = response.get("prefs_withwomen").equals("1");
            info.prefsWithStudents = response.get("prefs_withstudents").equals("1");
            info.prefsWithTeachers = response.get("prefs_withteachers").equals("1");

            info.notificationRadius = Double.parseDouble(response.get("notificationradius"));

            String oldHash = info.getProfileImageHash();
            info.setProfileImageHash(response.get("profilehash"));
            if (!oldHash.equals(info.getProfileImageHash())) {
                ResourceManager.setProfileImageDirty(info.email);
                info.setProfileImage(ResourceManager.getProfileImage(info.email));
            }

            // Load vehicle data.
            info.vehicle.capacity = Integer.parseInt(response.get("vehiclecapacity"));
            info.vehicle.make = response.get("vehiclemake");
            info.vehicle.color = response.get("vehiclecolor");
        } catch (Throwable t) {
            Log.e("error", "Failed to update user.");
        }

        info.unlock();
    }
}
