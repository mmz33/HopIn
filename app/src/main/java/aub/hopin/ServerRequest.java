package aub.hopin;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerRequest {
    public ServerRequestTag tag;
    public AtomicInteger status;
    public Object response;

    public ServerRequest(ServerRequestTag tag) {
        this.tag = tag;
        this.status = new AtomicInteger(ServerRequestStatus.Pending.ordinal());
        this.response = null;
    }
}
