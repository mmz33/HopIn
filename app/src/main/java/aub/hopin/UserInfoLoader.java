package aub.hopin;

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

        info.firstName      = response.get("firstname");
        info.lastName       = response.get("lastname");
        info.email          = response.get("email");
        info.age            = Integer.parseInt(response.get("age"));
        info.gender         = UserGender.fromSymbol(response.get("gender"));
        info.mode           = UserMode.fromSymbol(response.get("mode"));
        info.state          = UserState.fromSymbol(response.get("state"));
        info.role           = UserRole.fromSymbol(response.get("role"));
        info.phoneNumber    = response.get("phone");
        info.status         = response.get("status");
        info.address        = response.get("address");
        info.poBox          = response.get("pobox");
        info.showingAddress = response.get("showaddress").equals("1");
        info.showingPhone   = response.get("showphone").equals("1");
        info.active         = response.get("active").equals("1");

        info.latitude       = Double.parseDouble(response.get("latitude"));
        info.longitude      = Double.parseDouble(response.get("longitude"));

        String oldHash = info.getProfileImageHash();
        info.setProfileImageHash(response.get("profilehash"));
        if (!oldHash.equals(info.getProfileImageHash())) {
            ResourceManager.setProfileImageDirty(info.email);
            info.setProfileImage(ResourceManager.getProfileImage(info.email));
        }

        // Load vehicle data.
        info.vehicle.capacity = Integer.parseInt(response.get("vehiclecapacity"));
        info.vehicle.make     = response.get("vehiclemake");
        info.vehicle.color    = response.get("vehiclecolor");

        info.unlock();
    }
}
