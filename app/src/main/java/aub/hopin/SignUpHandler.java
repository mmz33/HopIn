package aub.hopin;

import java.net.Socket;

public class SignUpHandler extends ServerRequestHandler {
    UserInfo info;

    public SignUpHandler(Socket socket, ServerRequest request, UserInfo info) {
        super(socket, request);
        this.info = info;
    }

    public void handle() {
        writer.println(ServerRequestTag.SignUp.ordinal());
        writer.println(info.firstName);
        writer.println(info.lastName);
        writer.println(info.email);
        writer.println(info.age);
        writer.println(info.gender.ordinal());
        writer.println(info.mode.ordinal());
        writer.flush();
        success();
    }
}
