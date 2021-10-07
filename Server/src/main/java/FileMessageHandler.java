
import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileMessageHandler extends SimpleChannelInboundHandler<Command> {

    private static Path dirServer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        createServerDir();
        ctx.writeAndFlush(new ListRequest(dirServer));
        ctx.writeAndFlush(new PathRequest(dirServer.toString()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        //Все работает кроме: PathResponse , PathRequest , UpPathServer
        switch (cmd.getType()) {
            case LIST_RESPONSE:
                ctx.writeAndFlush(new ListRequest(dirServer));
                break;
            case FILE_SEND:
                Files.write(dirServer.resolve(((FileSend) cmd).getFileName()), ((FileSend) cmd).getBytes());
                ctx.writeAndFlush(new ListRequest(dirServer));
                break;
            case FILE_RESPONSE:
                String fileName = new String(((FileResponse) cmd).getFileName());
                if (Files.exists(dirServer.resolve(fileName))) {
                    ctx.writeAndFlush(new FileSend(fileName,
                            Files.readAllBytes(dirServer.resolve(fileName))));
                }

                break;
            case FILE_REQUEST:
                ctx.writeAndFlush(new FileRequest("Hello"));
                break;
            case FILE_DELETE:
                Files.delete(dirServer.resolve(((DeleteFile) cmd).getFileDel()));
                ctx.writeAndFlush(new ListRequest(dirServer));
                break;
        }

    }


    private void createServerDir() {
        File dir = new File(Paths.get("Server", "root").toString());
        if (!dir.exists()) {
            dir.mkdir();
            dirServer = dir.toPath();
        } else {
            dirServer = Paths.get("Server", "root");
        }
    }

}