package aub.hopin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Server {
    private static final String URL_ADDRESS = "hopin.herokuapp.com";
    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECTION_TIMEOUT = 10000;

    // Server url string.
    private static String urlString() {
        return "http://" + URL_ADDRESS;
    }

    // Constructs a request url.
    private static String buildRequest(String name, HashMap<String, String> args) {
        try {
            String link = "";
            for (String key : args.keySet()) {
                if (!link.equals("")) link += '&';
                link += key + "=" + URLEncoder.encode(args.get(key), "UTF-8");
            }
            return urlString() + "/" + name + "?" + link;
        } catch (UnsupportedEncodingException e) {
            Log.e("error", "Unsupported encoding used in url builder.");
            return null;
        }
    }

    // Reads all the contents of an InputStream into a String.
    private static String readContents(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = stream.read(buffer)) != -1) {
            builder.append(new String(buffer, 0, length));
        }
        return builder.toString();
    }

    // Parses a map using a string.
    public static HashMap<String, String> parseMap(String data) {
        Scanner scanner = new Scanner(data);
        HashMap<String, String> result = new HashMap<>();
        while (scanner.hasNextLine()) {
            String key = scanner.nextLine();
            if (!scanner.hasNextLine()) break;
            String val = scanner.nextLine();
            result.put(key, val);
        }
        scanner.close();
        return result;
    }

    // Reads the contents of an InputStream into a HashMap.
    private static HashMap<String, String> parseMap(InputStream stream) throws IOException {
        String contents = readContents(stream);
        Scanner scanner = new Scanner(contents);
        HashMap<String, String> result = new HashMap<>();
        while (scanner.hasNextLine()) {
            String key = scanner.nextLine();
            if (!scanner.hasNextLine()) break;
            String val = scanner.nextLine();
            result.put(key, val);
        }
        scanner.close();
        return result;
    }

    // Reads the contents of an InputStream into a List.
    private static ArrayList<String> parseList(InputStream stream) throws IOException {
        String contents = readContents(stream);
        Scanner scanner = new Scanner(contents);
        ArrayList<String> result = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String element = scanner.nextLine();
            result.add(element);
        }
        scanner.close();
        return result;
    }

    // Reads the contents of an InputStream into a List<Hashmap>.
    private static ArrayList<HashMap<String, String>> parseListMap(InputStream stream) throws IOException {
        String contents = readContents(stream);
        Scanner scanner = new Scanner(contents);
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        int count = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < count; ++i) {
            int xi = Integer.parseInt(scanner.nextLine());
            HashMap<String, String> hmap = new HashMap<>();
            for (int j = 0; j < xi; ++j) {
                String key = scanner.nextLine();
                String value = scanner.nextLine();
                hmap.put(key, value);
            }
            result.add(hmap);
        }
        scanner.close();
        return result;
    }

    // Retrieves a response from the server.
    private static String getResponse(String link) throws ConnectionFailureException {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200)
                return readContents(connection.getInputStream());

            throw new ConnectionFailureException();
        } catch (MalformedURLException e) {
            Log.e("error", "Bad server url");
            return null;
        } catch (IOException e) {
            Log.e("error", "Could not open url connection.");
            throw new ConnectionFailureException();
        }
    }

    // Retrieves a response from the server in the form of a map.
    private static HashMap<String, String> getResponseMap(String link) throws ConnectionFailureException {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200)
                return parseMap(connection.getInputStream());

            throw new ConnectionFailureException();
        } catch (MalformedURLException e) {
            Log.e("error", "Bad server url.");
            return null;
        } catch (IOException e) {
            Log.e("error", "Could not open url connection.");
            throw new ConnectionFailureException();
        }
    }

    // Retrieves a response from the server in the form of a list.
    private static ArrayList<String> getResponseList(String link) throws ConnectionFailureException {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200)
                return parseList(connection.getInputStream());

            throw new ConnectionFailureException();
        } catch (MalformedURLException e) {
            Log.e("error", "Bad server url.");
            return null;
        } catch (IOException e) {
            Log.e("error", "Could not open url connection.");
            throw new ConnectionFailureException();
        }
    }

    // Retrieves a response from the server in the form of a list<map>
    private static ArrayList<HashMap<String, String>> getResponseListMap(String link) throws ConnectionFailureException {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200)
                return parseListMap(connection.getInputStream());

            throw new ConnectionFailureException();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            throw new ConnectionFailureException();
        }
    }

    // Downloads an image from the server.
    private static Bitmap downloadBitmap(String url) throws ConnectionFailureException {
        try {
            URL myURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)myURL.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String contents = readContents(connection.getInputStream());
                return ImageUtils.decodeBase64(contents);
            } else {
                Log.e("error", "bad response code: " + responseCode);
            }

            throw new ConnectionFailureException();
        } catch (MalformedURLException e) {
            Log.e("images", "Error loading image: " + url);
            return null;
        } catch (IOException e) {
            Log.e("images", "Faced IO Exception: " + url);
            throw new ConnectionFailureException();
        } catch (OutOfMemoryError th) {
            Log.e("images", "Out of Memory: " + url);
            return null;
        } catch (Throwable t) {
            Log.e("images", "Error loading image: " + url);
            return null;
        }
    }

    public static Bitmap downloadProfileImage(String email) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("ssid", ActiveUser.getSessionId());
        return downloadBitmap(buildRequest("getprofileimg", args));
    }

    private static String getResponseUpload(String service, String email, Bitmap bmp) throws ConnectionFailureException {
        try {
            String encodedString = ImageUtils.encodeBase64(bmp);

            HashMap<String, String> args = new HashMap<>();
            args.put("email", email);
            args.put("ssid", ActiveUser.getSessionId());
            String link = buildRequest(service, args);
            if (link == null) throw new ConnectionFailureException();
            URL url = new URL(link);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("base64", encodedString);
            String query = builder.build().getEncodedQuery();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200)
                return readContents(connection.getInputStream());

            throw new ConnectionFailureException();
        } catch (MalformedURLException ex) {
            Log.e("error", "Bad server url");
            return null;
        } catch (IOException ioe) {
            Log.e("error", "Could not open url connection.");
            throw new ConnectionFailureException();
        }
    }

    public static String checkSession(String sessionId) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("ssid", sessionId);
        return getResponse(buildRequest("checksession", args));
    }

    public static String signUp(String firstName, String lastName, String email, int age, UserMode mode, UserGender gender, UserRole role) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("firstname", firstName);
        args.put("lastname", lastName);
        args.put("email", email);
        args.put("age", "" + age);
        args.put("mode", UserMode.toSymbol(mode));
        args.put("gender", UserGender.toSymbol(gender));
        args.put("role", UserRole.toSymbol(role));
        return getResponse(buildRequest("signup", args));
    }

    public static String signIn(String email, String password) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("password", password);
        return getResponse(buildRequest("signin", args));
    }

    public static String sendDestination(String email, double latitude, double longitude) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("lat", String.format("%.12f", latitude));
        args.put("lon", String.format("%.12f", longitude));
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("updestination", args));
    }

    //public static String sendPhoneNumber(String email, String phoneNumber) throws ConnectionFailureException {
    //    HashMap<String, String> args = new HashMap<>();
    //    args.put("email", email);
    //    args.put("phone", phoneNumber);
    //    args.put("ssid", ActiveUser.getSessionId());
    //    return getResponse(buildRequest("upphone", args));
    //}

    //public static String sendAddress(String email, String address) throws ConnectionFailureException {
    //    HashMap<String, String> args = new HashMap<>();
    //    args.put("email", email);
    //    args.put("address", address);
    //    args.put("ssid", ActiveUser.getSessionId());
    //    return getResponse(buildRequest("upaddress", args));
    //}

    //public static String sendPOBox(String email, String poBox) throws ConnectionFailureException {
    //    HashMap<String, String> args = new HashMap<>();
    //    args.put("email", email);
    //    args.put("pobox", poBox);
    //    args.put("ssid", ActiveUser.getSessionId());
    //    return getResponse(buildRequest("uppobox", args));
    //}

    //public static String sendProfilePicture(String email, String fileName) throws ConnectionFailureException {
    //    return getResponseUpload("upprofile", email, fileName);
    //}

    public static String sendProfilePicture(String email, Bitmap bmp) throws ConnectionFailureException {
        return getResponseUpload("upprofile", email, bmp);
    }

    public static String sendRidePreferences(String email, boolean withMen, boolean withWomen, boolean withStudents, boolean withTeachers, double notificationRadius) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("withmen", withMen? "1" : "0");
        args.put("withwomen", withWomen? "1" : "0");
        args.put("withstudents", withStudents? "1" : "0");
        args.put("withteachers", withTeachers? "1" : "0");
        args.put("notifradius", String.format("%.12f", notificationRadius));
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("uprideprefs", args));
    }

    public static String sendVehicleInfo(String email, String make, String color, int capacity) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("make", make);
        args.put("color", color);
        args.put("capacity", "" + capacity);
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("upvehicle", args));
    }

    public static String sendStatus(String email, String status) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("status", status);
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("updatestatus", args));
    }

    public static String sendStateModeSwitch(String email, UserMode mode, UserState state) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("mode", UserMode.toSymbol(mode));
        args.put("state", UserState.toSymbol(state));
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("switchstatemode", args));
    }

    public static String sendUserRating(String email, float stars) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("rating", String.format("%.2f", stars));
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("rate", args));
    }

    public static String sendProblem(String email, String problemMessage) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("message", problemMessage);
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("problem", args));
    }

    public static String sendFeedback(String email, String feedbackMessage) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("message", feedbackMessage);
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("feedback", args));
    }

    /**
     * Sends the current position of the user to the server.
     *
     * @param email     the user email
     * @param longitude the longitude of the user position
     * @param latitude  the latitude of the user position
     * @return          'OK' if the operation was successful
     */
    public static String sendGlobalPosition(String email, double longitude, double latitude) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("lat", String.format("%.12f", latitude));
        args.put("lon", String.format("%.12f", longitude));
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("sendpos", args));
    }

    //
    // This function will make the server get all the users that may be of interest
    // to the current user.
    //
    //  * You can only see active users.
    //  * All ride preferences apply to the query.
    //  * The only users you can see are those within your notification radius.
    //  * You can only see a user if his notification radius allows for it.
    //
    //      For a passive driver:
    //
    //          *   States:     Passive, Wanting
    //          *   Modes:      Passenger
    //
    //      For a passive passenger:
    //
    //          *   States:     Passive, Offering
    //          *   Modes:      Driver
    //
    //      For an offering driver:
    //
    //          *   States:     Wanting
    //          *   Modes:      Passenger
    //          *   Driver destination must be within 200 meters of passenger destinations.
    //
    //      For a wanting passenger:
    //
    //          *   States:     Offering
    //          *   Modes:      Driver
    //          *   Passenger destination must be within 200 meters of driver destination.
    //
    public static ArrayList<String> queryUsersOfInterest() throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("ssid", ActiveUser.getSessionId());
        args.put("email", ActiveUser.getEmail());

        ArrayList<HashMap<String, String>> response = getResponseListMap(buildRequest("queryinteresting", args));
        if (response == null) return new ArrayList<>();

        ArrayList<String> emails = new ArrayList<>();
        for (int i = 0; i < response.size(); ++i) {
            String email = response.get(i).get("email");
            emails.add(email);
        }

        return emails;
    }

    // This will get all the users from the server.
    public static ArrayList<String> queryAllUsers() throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("ssid", ActiveUser.getSessionId());
        args.put("email", ActiveUser.getEmail());
        return getResponseList(buildRequest("queryallusers", args));
    }

    public static HashMap<String, String> queryUserInfo(String email) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("ssid", ActiveUser.getSessionId());
        return getResponseMap(buildRequest("queryuser", args));
    }

    public static ArrayList<HashMap<String, String>> queryUsersInfo(ArrayList<String> emails) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("count", "" + emails.size());
        for (int i = 0; i < emails.size(); ++i) {
            args.put("email" + i, emails.get(i));
        }
        args.put("ssid", ActiveUser.getSessionId());
        return getResponseListMap(buildRequest("queryusers", args));
    }

    public static String confirmCode(String email, String code) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("code", code);
        return getResponse(buildRequest("confirm", args));
    }

    public static String sendUserInfoBundle(String email, HashMap<String, String> data) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("count", "" + data.size());
        args.put("ssid", ActiveUser.getSessionId());
        int i = 0;
        for (String key : data.keySet()) {
            args.put("key" + i, key);
            args.put("value" + i, data.get(key));
            i++;
        }
        return getResponse(buildRequest("sendbundle", args));
    }

    public static String leaveMessage(String email, String recipient, String message) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("recipient", recipient);
        args.put("message", message);
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("leavemessage", args));
    }

    public static HashMap<String, String> checkMessages(String email) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("ssid", ActiveUser.getSessionId());
        return getResponseMap(buildRequest("checkmessages", args));
    }

    public static String queryImageHash(String email) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("getimghash", args));
    }

    public static String logout() throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("ssid", ActiveUser.getSessionId());
        return getResponse(buildRequest("logout", args));
    }
}
