package aub.hopin;

import java.net.Socket;

public class AppRatingSendHandler extends ServerRequestHandler {
    private UserSession session;
    private int stars;

    public AppRatingSendHandler(Socket socket, ServerRequest request, UserSession session, int stars) {
        super(socket, request);
        this.session = session;
        this.stars = stars;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.writeInt(stars);
        this.writer.flush();

        this.success();
    }
}
