import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static  final Logger log = LoggerFactory.getLogger(Server.class);
    public static void main(String[] args) {
        try (ServerSocket sever = new ServerSocket(8189)){
log.debug("Server started...");
            while (true)
            {
                Socket socket = sever.accept();
                log.debug("Client accepted");
                Handler handler = new Handler(socket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
