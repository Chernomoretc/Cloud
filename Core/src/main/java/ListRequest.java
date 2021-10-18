import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ListRequest extends Command {
    //Отправляет клиенту список файлов
    private CommandType listRequest = CommandType.LIST_REQUEST;
    private byte[] bytes;

    public static byte[] sendServerDirInfo(Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
      if(Files.newDirectoryStream(path).iterator().hasNext())
      {
          Files.list(path).map(FileInfo::new).forEach(
                  s ->
                  {
                      sb.append(s.getType() + " ");
                      sb.append(s.getFilename() + " ");
                      sb.append(s.getSize() + "\n");
                  }
          );
      }else {
          sb.append("folder is empty");
      }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }


    public byte[] getBytes() {
        return bytes;
    }

    public ListRequest(Path path) throws IOException {
        bytes = sendServerDirInfo(path);
    }

    @Override
    public CommandType getType() {
        return listRequest;
    }
}
