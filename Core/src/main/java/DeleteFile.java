public class DeleteFile extends Command {
    private String fileDel;
    public DeleteFile(String fileDel) {
        this.fileDel = fileDel;
    }

    public String getFileDel() {
        return fileDel;
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_DELETE;
    }
}
