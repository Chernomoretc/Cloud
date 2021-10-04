import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ListResponse extends Command {

    //Запрос списка файлов сервера
   private CommandType listResponse = CommandType.LIST_RESPONSE;

    @Override
    public CommandType getType() {
        return listResponse;
    }
}
