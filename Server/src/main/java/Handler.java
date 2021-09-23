import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

public class Handler implements Runnable {
    private final Socket socket;
    private static final Logger log = LoggerFactory.getLogger(Server.class);


    public Handler(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try (DataInputStream is = new DataInputStream(socket.getInputStream());
             DataOutputStream os = new DataOutputStream(socket.getOutputStream());
             FileOutputStream fos = new FileOutputStream("server/root/test.txt"))
        {
while (true)
{
    File file = new File("server/root/test.txt");
    int size = is.read();
    byte [] bytes = new byte[size];
    is.read(bytes);
    log.debug("Received: {}");
    fos.write(bytes);
    os.flush();

}
        } catch (Exception e) {
            log.error("stacktrace: ",e);
        }

    }
}
