package aub.hopin;

import java.net.Socket;

public class ConfirmCodeHandler extends ServerRequestHandler {
    private String code;

    public ConfirmCodeHandler(Socket socket, ServerRequest request, String code) {
        super(socket, request);
        this.code = code;
    }

    public void handle() throws Exception {
        this.writer.writeUTF(this.code);
        this.writer.flush();

        this.respond(this.reader.readObject());
        this.success();
    }
}
