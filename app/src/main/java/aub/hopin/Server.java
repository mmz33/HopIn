package aub.hopin;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.InputStream;

public class Server {
    public static final String ipAddress = "141.138.181.20";
    public static final int portNumber = 2525;

    // Sends a sign up request to the server with all the
    // user sign up information.
    public static ServerRequest signUp(UserInfo info) {
        Log.d("", "Opening socket...");
        Socket socket = open();
        Log.d("", "Creating request...");
        ServerRequest request = new ServerRequest(ServerRequestTag.SignUp);
        Log.d("", "Creating handler...");
        SignUpHandler handler = new SignUpHandler(socket, request, info);
        Log.d("", "Dispatching handler...");
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest signIn(String email, String password) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.SignIn);
        SignInHandler handler = new SignInHandler(socket, request, email, password);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendSchedule(UserSession session, String filename) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.ChangeSchedule);
        ScheduleSendHandler handler = new ScheduleSendHandler(socket, request, session, filename);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendPhoneNumber(UserSession session, String phoneNumber) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.ChangePhoneNumber);
        MobileNumberSendHandler handler = new MobileNumberSendHandler(socket, request, session, phoneNumber);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendVehicleType(UserSession session, String vehicleType) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.ChangeVehicleType);
        VehicleTypeSendHandler handler = new VehicleTypeSendHandler(socket, request, session, vehicleType);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendVehiclePassengerCount(UserSession session, int passengers) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.ChangeVehiclePassengerCount);
        VehicleMaxPassengerCountSendHandler handler = new VehicleMaxPassengerCountSendHandler(socket, request, session, passengers);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendProfilePicture(UserSession session, String filename) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.ChangeProfilePicture);
        ProfilePictureSendHandler handler = new ProfilePictureSendHandler(socket, request, session, filename);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendStatus(UserSession session, String status) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.ChangeStatus);
        StatusSwitchSendHandler handler = new StatusSwitchSendHandler(socket, request, session, status);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendModeSwitch(UserSession session, UserMode mode) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.ChangeMode);
        ModeSwitchSendHandler handler = new ModeSwitchSendHandler(socket, request, session, mode);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest queryMapHistory(UserSession session) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.QueryMapHistory);
        MapHistoryQueryHandler handler = new MapHistoryQueryHandler(socket, request, session);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendUserRating(UserSession session, float stars) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.RateApp);
        AppRatingSendHandler handler = new AppRatingSendHandler(socket, request, session, stars);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendProblem(UserSession session, String problemMessage) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.ReportProblem);
        ProblemSendHandler handler = new ProblemSendHandler(socket, request, session, problemMessage);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest sendFeedback(UserSession session, String feedbackMessage) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.GiveFeedback);
        FeedbackSendHandler handler = new FeedbackSendHandler(socket, request, session, feedbackMessage);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest queryUserInfo(String email) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.QueryUserInfo);
        QueryUserInfoHandler handler = new QueryUserInfoHandler(socket, request, email);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    public static ServerRequest confirmCode(String code) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.ConfirmCode);
        ConfirmCodeHandler handler = new ConfirmCodeHandler(socket, request, code);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    // Gets an output stream to the socket.
    private static PrintWriter getOStream(Socket socket) {
        try {
            return new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Failed to get output stream: " + e.getMessage());
            return null;
        }
    }

    // Gets an input stream to the socket.
    private static InputStream getIStream(Socket socket) {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Failed to get input stream: " + e.getMessage());
            return null;
        }
    }

    // Opens up a socket to the server.
    private static Socket open() {
        try {
            return new Socket(ipAddress, portNumber);
        } catch (IOException e) {
            System.out.println("Connection to server failed. Message: " + e.getMessage());
            return null;
        }
    }
}
