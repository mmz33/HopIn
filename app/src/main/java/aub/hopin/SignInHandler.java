package aub.hopin;

import java.net.Socket;

public class SignInHandler extends ServerRequestHandler {
    private String email;
    private String password;

    public SignInHandler(Socket socket, ServerRequest request, String email, String password) {
        super(socket, request);
        this.email = email;
        this.password = password;
    }

    public void handle() throws Exception {
        this.writer.writeUTF(this.email);
        this.writer.writeUTF(this.password);
        this.writer.flush();

        this.respond(this.reader.readObject());
        this.success();
    }
}
