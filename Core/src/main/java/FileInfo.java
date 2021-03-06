import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//Класс для столбцов TableView
public class FileInfo {
    public enum FileType {
        FILE("F"), DIRECTORY("D"),IS_EMPTY("E");

        private String name;

        public String getName() {
            return name;
        }

        FileType(String name) {
            this.name = name;
        }
    }


    private String filename;
    private FileType type;
    private long size;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    //Конструктор для данных c сервера
    public FileInfo(String file) {
        if(file.equals("folder is empty"))
        {
            this.filename = file;
            this.type = FileType.IS_EMPTY;
            this.size = 0;
        }else {
            try {
                this.filename = file.split(" ")[1];
                this.size = Long.parseLong(file.split(" ")[2]);
                this.type = file.split(" ")[0].equals("FILE") ? FileType.FILE : FileType.DIRECTORY;
            } catch (Exception e) {
                throw new RuntimeException("Unable to create file info from path");
            }
        }

    }

//Конструктор для клиента
    public FileInfo(Path path) {
        try {
            this.filename = path.getFileName().toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if (this.type == FileType.DIRECTORY) {
                this.size = -1L;
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }
}
