public class FileSend extends Command {
    //Отправляем файл
    private String fileName;
    private CommandType fileSend = CommandType.FILE_SEND;
    private byte[] bytes;


    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public FileSend(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }

    @Override
    public CommandType getType() {
        return fileSend;
    }
}
