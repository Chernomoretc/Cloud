import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Handler implements Runnable {
    private final Socket socket;
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private String dirServer = "Server/root/";
    private static final int BUFFER_SIZE = 256;
    private final byte[] buffer = new byte[BUFFER_SIZE];


    public Handler(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try (DataInputStream is = new DataInputStream(socket.getInputStream());
             DataOutputStream os = new DataOutputStream(socket.getOutputStream()))
             {
             sendShowServerDir(os);

            while (true) {
                String fileName = is.readUTF();
                log.debug("Received fileName: {}", fileName);
                long size = is.readLong();
                log.debug("File size: {}", size);
                int read;
                try (OutputStream fos = Files.newOutputStream(Paths.get(dirServer, fileName))) {
                    for (int i = 0; i < (size + BUFFER_SIZE - 1) / BUFFER_SIZE; i++) {
                        read = is.read(buffer);
                        fos.write(buffer, 0 , read);
                    }
                    sendShowServerDir(os);
                } catch (Exception e) {
                    log.error("problem with file system");
                }
               // os.writeUTF("OK");


            }
        } catch (Exception e) {
            log.error("stacktrace: ", e);
        }


    }

    private void sendShowServerDir(DataOutputStream os) {
        try {
            List<String> showServerDir = Files.list(Paths.get(dirServer))
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());

            for (String s : showServerDir) {
                os.writeUTF(s);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
