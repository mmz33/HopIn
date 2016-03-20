package aub.hopin;

import java.net.Socket;

public class ProblemSendHandler extends ServerRequestHandler {
    private UserSession session;
    private String problemMessage;

    public ProblemSendHandler(Socket socket, ServerRequest request, UserSession session, String problemMessage) {
        super(socket, request);
        this.session = session;
        this.problemMessage = problemMessage;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.writeUTF(this.problemMessage);
        this.writer.flush();

        this.success();
    }
}
