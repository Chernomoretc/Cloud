public class FileResponse extends Command{
    private String fileName;
    private CommandType fileResponse = CommandType.FILE_RESPONSE;

    public String getFileName() {
        return fileName;
    }

    public FileResponse(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public CommandType getType() {

        return fileResponse;
    }
}
