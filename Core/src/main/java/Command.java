import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Command implements Serializable {

    CommandType type;
    private byte[] bytes;
    private Object fileInfoList;

    public String getFileName() {
        return fileName;
    }

    public Object getFileInfoList() {
        return fileInfoList;
    }

    private String fileName;

    public Command(CommandType type) {
        this.type = type;
    }

    public Command(CommandType type, Object fileInfoList) {
        this.type = type;
        this.fileInfoList = fileInfoList;
    }

    public Command(CommandType type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }

    public Command(CommandType type, String fileName, byte[] bytes) {
        this.type = type;
        this.fileName = fileName;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public CommandType getType() {
        return type;
    }
}
