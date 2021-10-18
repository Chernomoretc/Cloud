import java.io.IOException;
import java.nio.file.Path;

public enum CommandType {
    //request - ответ
    //response - запрос

    FILE_SEND,
    FILE_REQUEST,
    FILE_RESPONSE,
    LIST_REQUEST,
    LIST_RESPONSE,
    PATH_REQUEST,
    PATH_RESPONSE,
    FILE_DELETE,
    BIG_FILE_SEND,
    PATH_UP


}
