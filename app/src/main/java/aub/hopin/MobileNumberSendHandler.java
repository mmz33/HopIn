package aub.hopin;

import java.net.Socket;

public class MobileNumberSendHandler extends ServerRequestHandler {
    private String phoneNumber;
    private UserSession session;

    public MobileNumberSendHandler(Socket socket, ServerRequest request, UserSession session, String phoneNumber) {
        super(socket, request);
        this.phoneNumber = phoneNumber;
        this.session = session;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.writeUTF(this.phoneNumber);
        this.writer.flush();

        this.success();
    }
}
