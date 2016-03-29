package aub.hopin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
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
    private static final int CONNECTION_TIMEOUT = 5000;

    // Server url string.
    private static String urlString() {
        return "http://" + URL_ADDRESS;
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
    private static String getResponse(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            return responseCode == 200? readContents(connection.getInputStream()) : null;
        } catch (MalformedURLException e) {
            Log.e("", "Bad server url");
            return null;
         } catch (IOException e) {
            Log.e("", "Could not open url connection.");
            return null;
        }
    }

    // Retrieves a response from the server in the form of a map.
    private static HashMap<String, String> getResponseMap(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            return responseCode == 200? parseMap(connection.getInputStream()) : null;
        } catch (MalformedURLException e) {
            Log.e("", "Bad server url.");
            return null;
        } catch (IOException e) {
            Log.e("", "Could not open url connection.");
            return null;
        }
    }

    // Downloads an image from the server.
    public static Bitmap downloadBitmap(String url) {
        try {
            URL myURL = new URL(url);
            URLConnection connection = myURL.openConnection();
            InputStream is = connection.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(new BufferedInputStream(is));
            is.close();
            return image;
        } catch (FileNotFoundException e) {
            Log.e("", "Error loading image: " + url);
            return null;
        } catch (MalformedURLException e) {
            Log.e("", "Error loading image: " + url);
            return null;
        } catch (IOException e) {
            Log.e("", "Faced IO Exception: " + url);
            return null;
        } catch (OutOfMemoryError th) {
            Log.e("", "Out of Memory: " + url);
            return null;
        } catch (Throwable t) {
            Log.e("", "Error loading image: " + url);
            return null;
        }
    }

    public static Bitmap downloadProfileImage(String email) {
        String uname = email.replace('@', '_').replace('.', '_');
        return downloadBitmap(urlString() + "/" + uname + "/pp");
    }

    public static Bitmap downloadScheduleImage(String email) {
        String uname = email.replace('@', '_').replace('.', '_');
        return downloadBitmap(urlString() + "/" + uname + "/ss");
    }

    // Retrieves a response from the server after an upload.
    private static String getResponseUpload(String service, String email, String filename) {
        final String boundary = "******";
        final String sep = "--";

        try {
            URL url = new URL(urlString() + "/" + service + "?email=" + URLEncoder.encode(email, "UTF-8"));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("file", filename);

            FileInputStream fis = new FileInputStream(new File(filename));
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

            dos.writeBytes(sep + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"file\"\r\n");
            dos.writeBytes("\r\n");

            byte[] buffer = new byte[64 * 1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, length);
            }
            dos.writeBytes("\r\n");
            dos.writeBytes(sep + boundary + sep + "\r\n");
            dos.flush();
            fis.close();

            int responseCode = connection.getResponseCode();
            return responseCode == 200? readContents(connection.getInputStream()) : null;
        } catch (MalformedURLException e) {
            Log.e("", "Bad server url");
            return null;
        } catch (IOException e) {
            Log.e("", "Could not open url connection.");
            return null;
        }
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
            Log.e("", "Unsupported encoding used in url builder.");
            return null;
        }
    }

    public static String signUp(String firstName, String lastName, String email, int age, UserMode mode, UserGender gender) {
        HashMap<String, String> args = new HashMap<>();
        args.put("firstname", firstName);
        args.put("lastname", lastName);
        args.put("email", email);
        args.put("age", "" + age);
        args.put("mode", mode.toString());
        args.put("gender", gender.toString());
        return getResponse(buildRequest("signup", args ));
    }

    public static String signIn(String email, String password) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("password", password);
        return getResponse(buildRequest("signin", args));
    }

    public static String sendSchedule(String email, String filename) {
        return getResponseUpload("upschedule", email, filename);
    }

    public static String sendPhoneNumber(String email, String phoneNumber) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("phone", phoneNumber);
        return getResponse(buildRequest("upphone", args));
    }

    public static String sendProfilePicture(String email, String filename) {
        return getResponseUpload("upprofile", email, filename);
    }

    public static String sendStatus(String email, String status) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("status", status);
        return getResponse(buildRequest("updatestatus", args));
    }

    public static String sendModeSwitch(String email, UserMode mode) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("mode", mode.toString());
        return getResponse(buildRequest("switchmode", args));
    }

    public static String sendUserRating(String email, float stars) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("rating", String.format("%.2f", stars));
        return getResponse(buildRequest("rate", args));
    }

    public static String sendProblem(String email, String problemMessage) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("message", problemMessage);
        return getResponse(buildRequest("problem", args));
    }

    public static String sendFeedback(String email, String feedbackMessage) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("message", feedbackMessage);
        return getResponse(buildRequest("feedback", args));
    }

    public static HashMap<String, String> queryUserInfo(String email) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        return getResponseMap(buildRequest("queryuser", args));
    }

    public static String confirmCode(String email, String code) {
        HashMap<String, String> args = new HashMap<>();
        args.put("email", email);
        args.put("code", code);
        return getResponse(buildRequest("confirm", args));
    }
}
