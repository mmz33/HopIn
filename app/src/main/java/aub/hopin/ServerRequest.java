package aub.hopin;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerRequest {
    private ServerRequestTag tag;
    public AtomicInteger status;
    public Object response;

    public ServerRequest(ServerRequestTag tag) {
        this.tag = tag;
        this.status = new AtomicInteger(ServerRequestStatus.Pending.ordinal());
        this.response = null;
    }

    public ServerRequestTag getTag() {
        return this.tag;
    }
}
