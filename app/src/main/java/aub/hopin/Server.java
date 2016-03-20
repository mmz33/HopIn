package aub.hopin;

import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.InputStream;

public class Server {
    public static final String ipAddress = "192.168.192.161";
    public static final int portNumber = 2525;

    // Sends a sign up request to the server with all the
    // user sign up information.
    public static ServerRequest signUp(UserInfo info) {
        Socket socket = open();
        ServerRequest request = new ServerRequest(ServerRequestTag.SignUp);
        SignUpHandler handler = new SignUpHandler(socket, request, info);
        Thread thread = new Thread(handler);
        thread.start();
        return request;
    }

    // TODO
    // Implement the stubs.

    public static ServerRequest signIn(String email, String password) {
        return null;
    }

    public static ServerRequest sendSchedule(UserSession session, String filename) {
        return null;
    }

    public static ServerRequest sendPhoneNumber(UserSession session, String phoneNumber) {
        return null;
    }

    public static ServerRequest sendVehicleType(UserSession session, String vehicleType) {
        return null;
    }

    public static ServerRequest sendVehiclePassengerCount(UserSession session, int passengers) {
        return null;
    }

    public static ServerRequest sendProfilePicture(UserSession session, String filename) {
        return null;
    }

    public static ServerRequest sendStatus(UserSession session, String status) {
        return null;
    }

    public static ServerRequest sendModeSwitch(UserSession session, UserMode mode) {
        return null;
    }

    public static ServerRequest queryMapHistory(UserSession session) {
        return null;
    }

    public static ServerRequest sendUserRating(UserSession session, int stars) {
        return null;
    }

    public static ServerRequest sendProblem(UserSession session, String problemMessage) {
        return null;
    }

    public static ServerRequest sendFeedback(UserSession session, String feedbackMessage) {
        return null;
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

    //private static void close(Socket socket) {
    //    try {
    //        socket.close();
    //    } catch (IOException e) {}
    //}
    //private static void close(PrintWriter writer) {
    //    writer.close();
    //}
}
