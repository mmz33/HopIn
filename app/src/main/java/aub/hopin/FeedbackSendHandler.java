package aub.hopin;

import java.net.Socket;

public class FeedbackSendHandler extends ServerRequestHandler {
    private UserSession session;
    private String feedbackMessage;

    public FeedbackSendHandler(Socket socket, ServerRequest request, UserSession session, String feedbackMessage) {
        super(socket, request);
        this.session = session;
        this.feedbackMessage = feedbackMessage;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.writeUTF(this.feedbackMessage);
        this.writer.flush();

        this.success();
    }
}
