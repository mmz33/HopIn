package aub.hopin;

import java.net.Socket;

public class QueryUserInfoHandler extends ServerRequestHandler {
    private String email;

    public QueryUserInfoHandler(Socket socket, ServerRequest request, String email) {
        super(socket, request);
        this.email = email;
    }

    public void handle() throws Exception {
        this.writer.writeUTF(this.email);
        this.writer.flush();

        this.respond(this.reader.readObject());
        this.success();
    }
}
