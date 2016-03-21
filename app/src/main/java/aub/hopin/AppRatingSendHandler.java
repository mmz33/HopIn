package aub.hopin;

import java.net.Socket;

public class AppRatingSendHandler extends ServerRequestHandler {
    private UserSession session;
    private float stars;

    public AppRatingSendHandler(Socket socket, ServerRequest request, UserSession session, float stars) {
        super(socket, request);
        this.session = session;
        this.stars = stars;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.writeFloat(stars);
        this.writer.flush();

        this.success();
    }
}
