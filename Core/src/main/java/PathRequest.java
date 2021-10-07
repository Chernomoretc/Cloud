public class PathRequest extends Command {
    private String name;
    private CommandType pathRequest = CommandType.PATH_REQUEST;
    //Отправка клиенту директории

    public PathRequest(String currentServerDir) {
        this.name = currentServerDir;
    }

    public String getCurrentServerDir() {
        return name;
    }

    @Override
    public CommandType getType() {
        return pathRequest;
    }

}
