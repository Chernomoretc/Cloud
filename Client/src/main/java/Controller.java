import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.FileSystem;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
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

        rightPC.filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    if (rightPC.filesTable.getSelectionModel().getSelectedItem().getType().equals(FileInfo.FileType.DIRECTORY)) {
                        String pathName = rightPC.filesTable.getSelectionModel().getSelectedItem().getFilename();
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


        try {
            Socket socket = new Socket("localhost", 8111);
            is = new ObjectDecoderInputStream(socket.getInputStream());
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            rightPC.setOs(os);

            Thread demon = new Thread(() ->
            {
                try {
                    while (true) {
                        Command c = (Command) is.readObject();
                        switch (c.getType()) {
                            case LIST_REQUEST:
                                rightPC.updateListServer(new String(((ListRequest) c).getBytes()));
                                break;
                            case PATH_REQUEST:
                                rightPC.updatePathField(((PathRequest) c).getCurrentServerDir());
                                break;
                            case BIG_FILE_SEND:
                                BigFileSend bfs = (BigFileSend) c;
                                if (!Files.exists(Paths.get(leftPC.getCurrentPath()).resolve(bfs.getFileName()))) {
                                    Files.createFile(Paths.get(leftPC.getCurrentPath()).resolve(bfs.getFileName()));
                                }
                                Files.write(
                                        Paths.get(leftPC.getCurrentPath()).resolve(bfs.getFileName()),
                                        bfs.getBuff(),
                                        StandardOpenOption.APPEND);

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
        } catch (
                IOException e) {
            log.error("e= ", e);
        }


    }


    public void sendFile(ActionEvent actionEvent) throws IOException {
        String fileName = leftPC.getSelectedFilename();
        byte[] buff = new byte[10000];
        FileInputStream fis = new FileInputStream(
                (Paths.get(leftPC.getCurrentPath()).resolve(fileName)).toString());
        int read = 0;
        while ((read = fis.read(buff)) != -1) {
            if (read != buff.length) {

                byte[] endBuff = Arrays.copyOf(buff, read);
                buff = endBuff;
                os.writeObject(new BigFileSend(fileName, buff));
                os.flush();

            } else {
                os.writeObject(new BigFileSend(fileName, buff));
                os.flush();
            }
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
        //На удаление ошибка вылетает если файл использовался для передачи
        //Процесс не может получить доступ к файлу,
        // так как этот файл занят другим процессом
        String rightFileName = "";
        String leftFileName = "";
        if ((rightFileName = rightPC.getSelectedFilename()) != null) {
            System.out.println(rightFileName);
            os.writeObject(new DeleteFile(rightFileName));
            os.flush();
        } else if ((leftFileName = leftPC.getSelectedFilename()) != null) {
            Files.delete(Paths.get(leftPC.getCurrentPath()).resolve(leftFileName));
            leftPC.updateList(Paths.get(leftPC.getCurrentPath()));
        }
    }

}


