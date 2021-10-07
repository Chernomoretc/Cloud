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
    private Path dirClient;
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
        createDirClient();
        leftPC.updateList(dirClient);

        try {
            Socket socket = new Socket("localhost", 8190);
            is = new ObjectDecoderInputStream(socket.getInputStream());
            os = new ObjectEncoderOutputStream(socket.getOutputStream());


            Thread demon = new Thread(() ->
            {
                try {
                    while (true) {
                        Command c = (Command) is.readObject();
                        switch (c.getType()) {
                            case LIST_REQUEST:
                                rightPC.updateListServer(new String(((ListRequest) c).getBytes()));
                                break;
                            case FILE_REQUEST:
                                System.out.println(((FileRequest) c).getFileName());
                                break;
                            case PATH_REQUEST:
                                rightPC.updatePathField(((PathRequest) c).getCurrentServerDir());
                                break;
                            case FILE_SEND:
                                Files.write(Paths.get(leftPC.getCurrentPath()).resolve(((FileSend) c).getFileName()),
                                        ((FileSend) c).getBytes());
                                leftPC.updateList(Paths.get(leftPC.getCurrentPath()));
                                break;
                        }

                    }
                } catch (Exception e) {
                    log.error("exception while read from input stream");
                    e.printStackTrace();
                }
            });
            demon.start();
        } catch (IOException e) {
            log.error("e= ", e);
        }
    }


    public void sendListResponse(ActionEvent actionEvent) throws IOException {
        os.writeObject(new ListResponse());
    }

    public void sendFile(ActionEvent actionEvent) throws IOException {

        String fileName = leftPC.getSelectedFilename();
        if (Files.exists(Paths.get(leftPC.getCurrentPath()).resolve(fileName))) {
            System.out.println("ok");
            os.writeObject(new FileSend(fileName,
                    Files.readAllBytes(Paths.get(leftPC.getCurrentPath()).resolve(fileName))));
            os.flush();
        }
    }

    public void uploadFile(ActionEvent actionEvent) throws IOException {
        String fileName = rightPC.getSelectedFilename();
        os.writeObject(new FileResponse(fileName));
        os.flush();
    }

    private void createDirClient() {
        File dir = new File(Paths.get("Client", "root").toString());
        if (!dir.exists()) {
            dir.mkdir();
            dirClient = dir.toPath();
        } else {
            dirClient = Paths.get("Client", "root");
        }
    }


    public void deleteFile(ActionEvent actionEvent) throws IOException {
        String rightFileName = rightPC.getSelectedFilename();
        String leftFileName = leftPC.getSelectedFilename();
        if (!leftFileName.isEmpty()) {
            Files.delete(Paths.get(leftPC.getCurrentPath()).resolve(leftFileName));
            leftPC.updateList(Paths.get(leftPC.getCurrentPath()));
            leftFileName = null;
        }
        else if (!rightFileName.equals(null)) {
            os.writeObject(new DeleteFile(rightFileName));
            os.flush();
            rightFileName =null;
        }
    }
}


