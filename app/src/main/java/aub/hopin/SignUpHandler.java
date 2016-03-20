package aub.hopin;

import java.io.IOException;
import java.net.Socket;

public class SignUpHandler extends ServerRequestHandler {
    private UserInfo info;

    public SignUpHandler(Socket socket, ServerRequest request, UserInfo info) {
        super(socket, request);
        this.info = info;
    }

    public void handle() throws Exception {
        this.writer.writeUTF(this.info.firstName);
        this.writer.writeUTF(this.info.lastName);
        this.writer.writeUTF(this.info.email);
        this.writer.writeInt(this.info.age);
        this.writer.writeInt(this.info.gender.ordinal());
        this.writer.writeInt(this.info.mode.ordinal());
        this.writer.flush();

        this.respond(this.reader.readObject());
        this.success();
    }
}
