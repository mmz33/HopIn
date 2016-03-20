package aub.hopin;

import java.net.Socket;

public class VehicleMaxPassengerCountSendHandler extends ServerRequestHandler {
    private UserSession session;
    private int passengerCount;

    public VehicleMaxPassengerCountSendHandler(Socket socket, ServerRequest request, UserSession session, int passengerCount) {
        super(socket, request);
        this.session = session;
        this.passengerCount = passengerCount;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.writeInt(this.passengerCount);
        this.writer.flush();

        this.success();
    }
}
