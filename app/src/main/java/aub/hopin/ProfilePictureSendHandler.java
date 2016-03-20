package aub.hopin;

import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

public class ProfilePictureSendHandler extends ServerRequestHandler {
    private UserSession session;
    private String filename;

    public ProfilePictureSendHandler(Socket socket, ServerRequest request, UserSession session, String filename) {
        super(socket, request);
        this.session = session;
        this.filename = filename;
    }

    public void handle() throws Exception {
        File file = new File(this.filename);
        int size = (int)file.length();
        byte[] bytes = new byte[size];

        BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file));
        buffer.read(bytes, 0, bytes.length);
        buffer.close();

        String extension = this.filename.substring(this.filename.lastIndexOf('.') + 1);

        this.writer.writeObject(this.session);
        this.writer.writeUTF(extension);
        this.writer.write(bytes);
        this.writer.flush();

        this.success();
    }
}
