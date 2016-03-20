package aub.hopin;

import java.net.Socket;

public class ModeSwitchSendHandler extends ServerRequestHandler {
    private UserSession session;
    private UserMode mode;

    public ModeSwitchSendHandler(Socket socket, ServerRequest request, UserSession session, UserMode mode) {
        super(socket, request);
        this.session = session;
        this.mode = mode;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.writeInt(this.mode.ordinal());
        this.writer.flush();

        this.success();
    }
}
