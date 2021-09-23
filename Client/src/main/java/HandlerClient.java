import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

public class HandlerClient implements Runnable {
    private final Socket socket;
//    private static final Logger log = LoggerFactory.getLogger(Server.class);


    public HandlerClient(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try (DataInputStream is = new DataInputStream(socket.getInputStream());
             DataOutputStream os = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream("test.txt"))
        {
            while (true)
            {
                int size = fis.read();
                byte [] bytes = new byte[size];
                fis.read(bytes);
//                log.debug("Received: {}");
                os.write(bytes);
                os.flush();

            }
        } catch (Exception e) {
//            log.error("stacktrace: ",e);
        }

    }
}

