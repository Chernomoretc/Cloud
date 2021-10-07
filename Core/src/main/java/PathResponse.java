public class PathResponse extends Command {
    private CommandType pathResponse = CommandType.PATH_RESPONSE;

    //Запрос директории сервера
    @Override
    public CommandType getType() {
        return pathResponse;
    }

}
