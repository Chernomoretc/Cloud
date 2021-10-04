public class FileRequest extends Command {
    private String fileName;
    private CommandType fileRequest = CommandType.FILE_REQUEST;

    public FileRequest(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public CommandType getType() {
        return fileRequest;
    }
}
