package aub.hopin;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

public abstract class ServerRequestHandler implements Runnable {
    private ServerRequest request;
    private Socket socket;
    protected PrintWriter writer;
    protected InputStream reader;

    public ServerRequestHandler(Socket socket) {
        this.socket = socket;
        this.request = null;
        this.writer = null;
        this.reader = null;
    }

    public ServerRequestHandler(Socket socket, ServerRequest request) {
        this.socket = socket;
        this.request = request;
        this.writer = null;
        this.reader = null;
    }

    public void fail() { this.request.status.set(ServerRequestStatus.Failure.ordinal()); }
    public void success() { this.request.status.set(ServerRequestStatus.Success.ordinal()); }

    public void run() {
        if (socket != null) {
            this.writer = getOStream();
            this.reader = getIStream();
            if (this.writer != null && this.reader != null) {
                handle();
            } else {
                fail();
            }
        } else {
            fail();
        }
    }

    public abstract void handle();

    private InputStream getIStream() {
        try {
            return this.socket.getInputStream();
        } catch (IOException ioe) {
            ErrorLogger.error("Could not open input stream for socket.");
            return null;
        }
    }

    private PrintWriter getOStream() {
        try {
            return new PrintWriter(this.socket.getOutputStream());
        } catch (IOException ioe) {
            ErrorLogger.error("Could not open output stream for socket.");
            return null;
        }
    }
}
