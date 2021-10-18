import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControllerFilePanel implements Initializable {
    ObjectEncoderOutputStream os;
    @FXML
    TableView<FileInfo> filesTable;

    @FXML
    TextField pathField;

    public void setOs(ObjectEncoderOutputStream os) {
        this.os = os;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Имя");
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(120);
        filesTable.getColumns().addAll(fileTypeColumn, filenameColumn, fileSizeColumn);
        filesTable.getSortOrder().add(fileTypeColumn);
        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(pathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFilename());
                    if (Files.isDirectory(path)) {
                        updateList(path);
                    }
                }
            }
        });
        //updateList(Paths.get("."));
    }


    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void updateListServer(String files) {
        filesTable.getItems().clear();
        if (files.equals("folder is empty")) {
            filesTable.getItems().add(new FileInfo(files));
        } else {
            String[] f = files.split("\n");
            for (String s : f) {
                if (s != null) filesTable.getItems().add(new FileInfo(s));
            }

            filesTable.sort();
        }
    }

    public void btnPathUpAction(ActionEvent actionEvent) throws IOException {
        String upperPath = pathField.getText();
        if (upperPath != null && upperPath.startsWith("Client") && !upperPath.endsWith("root")) {
            updateList(Paths.get(upperPath).getParent());
        } else if (upperPath != null && upperPath.startsWith("Server") && !upperPath.endsWith("root")) {
            os.writeObject(new PathUp());
            os.flush();
        }
    }


    public String getSelectedFilename() {
        if (!filesTable.isFocused()) {
            return null;
        }
        return filesTable.getSelectionModel().getSelectedItem().getFilename();
    }

    public String getCurrentPath() {
        return pathField.getText();
    }

    public void updatePathField(String s) {
        pathField.clear();
        pathField.setText(s);
    }


}
