public class PathResponse extends Command {
    private  String dir;
    private CommandType pathResponse = CommandType.PATH_RESPONSE;

    public PathResponse(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }

    //Запрос директории у сервера
    @Override
    public CommandType getType() {
        return pathResponse;
    }

}
