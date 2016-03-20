package aub.hopin;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ServerRequestHandler implements Runnable {
    private ServerRequest request;
    private Socket socket;
    protected ObjectOutputStream writer;
    protected ObjectInputStream reader;

    public ServerRequestHandler(Socket socket, ServerRequest request) {
        this.socket = socket;
        this.request = request;
        this.writer = null;
        this.reader = null;
    }

    // Mark the request as failed.
    public void fail() {
        this.request.status.set(ServerRequestStatus.Failure.ordinal());
    }

    // Mark the request as successfully handled.
    public void success() {
        this.request.status.set(ServerRequestStatus.Success.ordinal());
    }

    // Set the server response Object.
    public void respond(Object o) {
        request.response = o;
    }

    public void run() {
        if (this.socket != null) {
            this.writer = this.getOStream();
            this.reader = this.getIStream();
            if (this.writer != null && this.reader != null) {
                try {
                    this.writer.writeInt(this.request.getTag().ordinal());
                    this.handle();
                } catch (Exception e) {
                    ErrorLogger.error("Handler failed.");
                    this.fail();
                }
            } else {
                this.fail();
            }
        } else {
            this.fail();
        }
    }

    public abstract void handle() throws Exception;

    private ObjectInputStream getIStream() {
        try {
            return new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException ioe) {
            ErrorLogger.error("Could not open input stream for socket.");
            return null;
        }
    }

    private ObjectOutputStream getOStream() {
        try {
            return new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException ioe) {
            ErrorLogger.error("Could not open output stream for socket.");
            return null;
        }
    }
}
