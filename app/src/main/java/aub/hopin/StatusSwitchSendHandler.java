package aub.hopin;

import java.net.Socket;

public class StatusSwitchSendHandler extends ServerRequestHandler {
    private UserSession session;
    private String status;

    public StatusSwitchSendHandler(Socket socket, ServerRequest request, UserSession session, String status) {
        super(socket, request);
        this.session = session;
        this.status = status;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.writeUTF(this.status);
        this.writer.flush();

        this.success();
    }
}
