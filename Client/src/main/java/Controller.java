import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);


    private Path dirClient = Paths.get("Client", "root");
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    ControllerFilePanel leftPC;
    ControllerFilePanel rightPC;
    @FXML
    VBox leftPanel, rightPanel;




    @Override
    public void initialize(URL location, ResourceBundle resources) {

         leftPC = (ControllerFilePanel) leftPanel.getProperties().get("ctrl");
         rightPC = (ControllerFilePanel) rightPanel.getProperties().get("ctrl");
        leftPC.updateList(dirClient);
        leftPC.updatePathField("Client/root/");
        rightPC.updatePathField("Server/root/");


        try {
            Socket socket = new Socket("localhost", 8189);
            is = new ObjectDecoderInputStream(socket.getInputStream());
            os = new ObjectEncoderOutputStream(socket.getOutputStream());


            Thread demon = new Thread(() ->
            {
                try {
                    while (true) {

                        Command c = (Command) is.readObject();
                        switch (c.getType()) {
                            case LIST_REQUEST:
                                rightPC.updateListServer(new String(c.getBytes()));


                        }

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

    public void sendListResponse(ActionEvent actionEvent) throws IOException {
        os.writeObject(new Command(CommandType.LIST_RESPONSE));
    }

    public void sendFile(ActionEvent actionEvent) throws IOException {


        if(Files.exists(dirClient.resolve(leftPC.fileName)))
        {
            os.writeObject(new Command(CommandType.FILE_SEND,leftPC.fileName,
                    Files.readAllBytes(dirClient.resolve(leftPC.fileName))));

        }
    }
}


