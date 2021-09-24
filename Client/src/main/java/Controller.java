import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    public ListView<String> listViewClient;
    public TextField input;
    public ListView<String> listViewServer;

    private static byte[] buffer = new byte[1024];
    private String dirClient = "Client/root/";
    private DataInputStream is;
    private DataOutputStream os;
    private FileInputStream fis;


    public void send(ActionEvent actionEvent) throws Exception {
        String fileName = input.getText();
        input.clear();
        sendFile(fileName);
        listViewServer.getItems().clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            showCurrentDir();
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread demon = new Thread(() ->
            {
                try {
                    while (true) {

                        String msg = is.readUTF();
                        Platform.runLater(() -> listViewServer.getItems().add(msg));
                        log.debug("received: {}", msg);

                    }
                } catch (Exception e) {
                    log.error("exception while read from input stream");
                }


            });
            demon.start();
        } catch (IOException e) {
            log.error("e= ", e);
        }
    }

    private void showCurrentDir() throws IOException {
        listViewClient.getItems().addAll(
                Files.list(Paths.get(dirClient))
                        .map(p -> p.getFileName().toString())
                        .collect(Collectors.toList())
        );
    }

    private void sendFile(String fileName) throws IOException {
        Path file = Paths.get(dirClient, fileName);
        if (Files.exists(file)) {
            long size = Files.size(file);

            os.writeUTF(fileName);
            os.writeLong(size);

            InputStream fileStream = Files.newInputStream(file);
            int read;
            while ((read = fileStream.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
        } else {
            os.writeUTF(fileName);
            os.flush();
        }
    }
}


