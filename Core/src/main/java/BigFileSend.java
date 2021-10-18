

import java.io.FileNotFoundException;
import java.io.IOException;

public class BigFileSend extends Command {
    public String getFileName() {
        return fileName;
    }

    private String fileName;
    private byte[] buff;

    public BigFileSend(String fileName, byte[] buf) throws FileNotFoundException {
        this.fileName = fileName;
        this.buff = buf;


    }

    public byte[] getBuff() throws IOException {
        return buff;
    }


    @Override
    public CommandType getType() {
        return CommandType.BIG_FILE_SEND;
    }
}
