package aub.hopin;

import java.net.Socket;

public class VehicleTypeSendHandler extends ServerRequestHandler {
    private UserSession session;
    private String vehicleType;

    public VehicleTypeSendHandler(Socket socket, ServerRequest request, UserSession session, String vehicleType) {
        super(socket, request);
        this.session = session;
        this.vehicleType = vehicleType;
    }

    public void handle() throws Exception {
        this.writer.writeObject(this.session);
        this.writer.writeUTF(this.vehicleType);
        this.writer.flush();

        this.success();
    }
}
