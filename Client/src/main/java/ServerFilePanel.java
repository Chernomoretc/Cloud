import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerFilePanel extends ControllerFilePanel implements Initializable {
    private ObjectEncoderOutputStream os;

    public ServerFilePanel(ObjectEncoderOutputStream os) {
        this.os = os;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    if (filesTable.getSelectionModel().getSelectedItem().getType().equals(FileInfo.FileType.DIRECTORY)) {
                        String pathName = filesTable.getSelectionModel().getSelectedItem().getFilename();
                        try {
                            System.out.println(pathName);
                            os.writeObject(new PathResponse(pathName));
                            os.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }



}
