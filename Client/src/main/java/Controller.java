import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    public ListView<String> listView;
    public TextField input;

    private DataInputStream is;
    private DataOutputStream os;
    private FileInputStream fis;


    public void send(ActionEvent actionEvent) throws Exception {
        String msg = input.getText();
        input.clear();
        fis = new FileInputStream(msg);
        int size =  fis.read();
        byte [] bytes  = new byte[size];
        fis.read(bytes);
        os.write(bytes);
        os.flush();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread demon = new Thread(() ->
            {
                try {
                    while (true) {
                        String msg = is.readUTF();
                        log.debug("received: {}", msg);
                        Platform.runLater(() -> listView.getItems().add(msg));
                    }
                } catch (Exception e) {
                    log.error("exception while read from input stream");
                }


            });
        } catch (IOException e) {
            log.error("e= ", e);
        }
    }
}
