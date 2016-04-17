package aub.hopin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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

    // Retrieves a response from the server.
    private static String getResponse(String link) throws ConnectionFailureException {
        try {
            //Log.i("error", "Starting to get response.");
            //Log.i("error", "Creating connection: " + link);
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            //Log.i("error", "Connecting...");
            connection.connect();

            int responseCode = connection.getResponseCode();
            //Log.i("error", "Response code: " + responseCode);
            String str = responseCode == 200? readContents(connection.getInputStream()) : null;
            //Log.i("error", "Data: " + str);

            return str;
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
            //Log.i("error", "Starting to get response map.");
            //Log.i("error", "Creating connection: " + link);
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            //Log.i("error", "Connecting...");
            connection.connect();

            int responseCode = connection.getResponseCode();
            //Log.i("error", "Response code: " + responseCode);
            HashMap<String, String> res = parseMap(connection.getInputStream());
            //Log.i("error", "Read map:");
            //Log.i("error", "-----");
            //for (String key : res.keySet()) {
            //    Log.i("error", key + " -> " + res.get(key));
            //}
            //Log.i("error", "-----");
            return res;
        } catch (MalformedURLException e) {
            Log.e("error", "Bad server url.");
            return null;
        } catch (IOException e) {
            Log.e("error", "Could not open url connection.");
            throw new ConnectionFailureException();
        }
    }

    // Downloads an image from the server.
    public static Bitmap downloadBitmap(String url) {
        try {
            //Log.i("images", "About to download bitmap: " + url);
            URL myURL = new URL(url);
            URLConnection connection = myURL.openConnection();
            InputStream is = connection.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(new BufferedInputStream(is));
            //Log.i("images", "Successfully downloaded bitmap.");
            is.close();
            return image;
        } catch (MalformedURLException e) {
            Log.e("images", "Error loading image: " + url);
            return null;
        } catch (IOException e) {
            Log.e("images", "Faced IO Exception: " + url);
            return null;
        } catch (OutOfMemoryError th) {
            Log.e("images", "Out of Memory: " + url);
            return null;
        } catch (Throwable t) {
            Log.e("images", "Error loading image: " + url);
            return null;
        }
    }

    public static Bitmap downloadProfileImage(String email) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        return downloadBitmap(buildRequest("getprofileimg", args));
    }

    public static Bitmap downloadScheduleImage(String email) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        return downloadBitmap(buildRequest("getscheduleimg", args));
    }

    // Retrieves a response from the server after an upload.
    private static String getResponseUpload(String service, String email, String filename) throws ConnectionFailureException {
        final String lineEnd = "\r\n";
        final String twoHyphens = "--";
        final String boundary =  "*****";
        final int bufferSize = 64 * 1024;

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filename));
            URL url = new URL(urlString() + "/" + service + "?email=" + URLEncoder.encode(email, "UTF-8"));


            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + filename +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            byte[] buffer = new byte[bufferSize];

            //int total = 0;

            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer, 0, bufferSize)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
                //bytesAvailable = fileInputStream.available();
                //bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                //total += bytesRead;
            }

            //Log.i("error", "Wrote " + total + " bytes to output stream!");

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            outputStream.flush();

            int responseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + responseCode);

            String message = "";

            if (responseCode == 200) {
                message = readContents(connection.getInputStream());
            }

            fileInputStream.close();
            outputStream.close();

            return message;

        } catch (MalformedURLException ex) {
            Log.e("error", "Bad server url");
            return null;
        } catch (IOException ioe) {
            Log.e("error", "Could not open url connection.");
            throw new ConnectionFailureException();
        }
    }

    public static String getModeString(UserMode mode) {
        switch (mode) {
            case DriverMode: return "D";
            case PassengerMode: return "P";
            case Unspecified: return "U";
        }
        return "?";
    }

    public static String getGenderString(UserGender gender) {
        switch (gender) {
            case Female: return "F";
            case Male: return "M";
            case Other:
                return "O";
        }
        return "?";
    }

    public static String getStateString(UserState state) {
        switch (state) {
            case Passive:
                return "P";
            case Offering: return "O";
            case Wanting: return "W";
        }
        return "?";
    }

    public static String signUp(String firstName, String lastName, String email, int age, UserMode mode, UserGender gender) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("firstname", firstName);
        args.put("lastname", lastName);
        args.put("email", email);
        args.put("age", "" + age);
        args.put("mode", getModeString(mode));
        args.put("gender", getGenderString(gender));
        return getResponse(buildRequest("signup", args));
    }

    public static String signIn(String email, String password) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("password", password);
        return getResponse(buildRequest("signin", args));
    }

    public static String sendSchedule(String email, String filename) throws ConnectionFailureException {
        return getResponseUpload("upschedule", email, filename);
    }

    public static String sendPhoneNumber(String email, String phoneNumber) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("phone", phoneNumber);
        return getResponse(buildRequest("upphone", args));
    }

    public static String sendAddress(String email, String address) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("address", address);
        return getResponse(buildRequest("upaddress", args));
    }

    public static String sendPOBox(String email, String poBox) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("pobox", poBox);
        return getResponse(buildRequest("uppobox", args));
    }

    public static String sendProfilePicture(String email, String fileName) throws ConnectionFailureException {
        return getResponseUpload("upprofile", email, fileName);
    }

    public static String sendProfilePicture(String email, Bitmap bitmap) throws ConnectionFailureException {
        return null;
    }

    public static String sendStatus(String email, String status) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("status", status);
        return getResponse(buildRequest("updatestatus", args));
    }

    public static String sendModeSwitch(String email, UserMode mode) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("mode", getModeString(mode));
        return getResponse(buildRequest("switchmode", args));
    }

    public static String sendStateSwitch(String email, UserState state) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("state", getStateString(state));
        return getResponse(buildRequest("switchstate", args));
    }

    public static String sendUserRating(String email, float stars) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("rating", String.format("%.2f", stars));
        return getResponse(buildRequest("rate", args));
    }

    public static String sendProblem(String email, String problemMessage) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("message", problemMessage);
        return getResponse(buildRequest("problem", args));
    }

    public static String sendFeedback(String email, String feedbackMessage) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("message", feedbackMessage);
        return getResponse(buildRequest("feedback", args));
    }

    public static String showPhone(String email, boolean b) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("show", b? "1" : "0");
        return getResponse(buildRequest("showphone", args));
    }

    public static String showAddress(String email, boolean b) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("show", b ? "1" : "0");
        return getResponse(buildRequest("showaddress", args));
    }

    public static String sendGlobalPosition(String email, double longitude, double latitude) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("lat", String.format("%.12f", latitude));
        args.put("lon", String.format("%.12f", longitude));
        return getResponse(buildRequest("sendpos", args));
    }

    public static HashMap<String, String> queryActiveUsersAndPositions() throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        return getResponseMap(buildRequest("queryactive", args));
    }

    public static HashMap<String, String> queryUserInfo(String email) throws ConnectionFailureException {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        return getResponseMap(buildRequest("queryuser", args));
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
        int i = 0;
        for (String key : data.keySet()) {
            args.put("key" + i, key);
            args.put("value" + i, data.get(key));
            i++;
        }
        return getResponse(buildRequest("sendbundle", args));
    }
}
