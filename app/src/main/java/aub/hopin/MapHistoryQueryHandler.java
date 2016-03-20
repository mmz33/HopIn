package aub.hopin;

import java.net.Socket;

public class MapHistoryQueryHandler extends ServerRequestHandler {
    private UserSession session;

    public MapHistoryQueryHandler(Socket socket, ServerRequest request, UserSession session) {
        super(socket, request);
        this.session = session;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.flush();

        this.success();
    }
}
