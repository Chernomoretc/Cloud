

public class ListResponse extends Command {

    //Запрос списка файлов сервера
   private CommandType listResponse = CommandType.LIST_RESPONSE;

    @Override
    public CommandType getType() {
        return listResponse;
    }
}
