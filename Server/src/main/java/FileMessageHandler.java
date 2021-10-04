
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileMessageHandler extends SimpleChannelInboundHandler<Command> {

    private static final Path dirServer = Paths.get("Server", "root");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ListRequest(dirServer));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        // TODO: 23.09.2021 Разработка системы команд
        // Сервер пока только получает файлы и отправляет содержимое серверной папки
        switch (cmd.getType()) {
            case LIST_RESPONSE:
                ctx.writeAndFlush(new ListRequest(dirServer));
                break;
            case FILE_SEND:
                Files.write(dirServer.resolve(((FileSend) cmd).getFileName()), ((FileSend) cmd).getBytes());
                ctx.writeAndFlush(new ListRequest(dirServer));
                break;
            case PATH_RESPONSE:
                ctx.writeAndFlush(new PathRequest());
            case FILE_RESPONSE:

        }

    }


}