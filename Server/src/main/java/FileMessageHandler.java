

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileMessageHandler extends SimpleChannelInboundHandler<Command> {

    private static final Path dirServer = Paths.get("Server", "root");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        // TODO: 23.09.2021 Разработка системы команд

        switch (cmd.getType()) {
            case LIST_RESPONSE:
                ctx.write(new Command(CommandType.LIST_REQUEST, showServerDir().getBytes(StandardCharsets.UTF_8)));
                ctx.flush();
                break;

            case FILE_SEND:
                Files.write(dirServer.resolve(cmd.getFileName()), cmd.getBytes());
                ctx.write(new Command(CommandType.LIST_REQUEST, showServerDir().getBytes(StandardCharsets.UTF_8)));

                ctx.flush();
                break;

        }

    }

    private static String showServerDir() throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.list(dirServer).map(FileInfo::new).forEach(
                s -> fileInfoAdapter(sb, s)
        );
        return sb.toString();
    }

    public static void fileInfoAdapter(StringBuilder sb, FileInfo f) {
        sb.append(f.getType() + " ");
        sb.append(f.getFilename() + " ");
        sb.append(f.getSize() + "\n");
    }
}