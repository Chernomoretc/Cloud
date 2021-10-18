
import java.io.File;

import java.io.FileInputStream;
import java.nio.file.*;
import java.util.Arrays;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileMessageHandler extends SimpleChannelInboundHandler<Command> {

    private static Path dirServer;
    private static Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        createServerDir();
        ctx.writeAndFlush(new ListRequest(dirServer));
        ctx.writeAndFlush(new PathRequest(dirServer.toString()));
        currentDir = dirServer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        switch (cmd.getType()) {
            case LIST_RESPONSE:
                ctx.writeAndFlush(new ListRequest(currentDir));
                break;
            case FILE_RESPONSE:
                String fileName = ((FileResponse) cmd).getFileName();
                if (Files.exists(currentDir.resolve(fileName))) {
                    byte[] buff = new byte[10000];
                    FileInputStream fis = new FileInputStream(
                            (currentDir.resolve(fileName)).toString());
                    int read = 0;
                    while ((read = fis.read(buff)) != -1) {
                        if (read != buff.length) {
                            byte[] endBuff = Arrays.copyOf(buff, read);
                            buff = endBuff;
                            ctx.writeAndFlush(new BigFileSend(fileName, buff));
                        } else {
                            ctx.writeAndFlush(new BigFileSend(fileName, buff));
                        }
                    }
                }
                break;

            case FILE_DELETE:
                Files.delete(currentDir.resolve(((DeleteFile) cmd).getFileDel()));
                System.out.println();
                ctx.writeAndFlush(new ListRequest(currentDir));
                break;
            case PATH_RESPONSE:
                currentDir = dirServer.resolve(Paths.get(((PathResponse) cmd).getDir()));
                ctx.writeAndFlush(new ListRequest(currentDir));
                ctx.writeAndFlush(new PathRequest(currentDir.toString()));
                break;
            case PATH_UP:
                currentDir = currentDir.getParent();
                ctx.writeAndFlush(new ListRequest(currentDir));
                ctx.writeAndFlush(new PathRequest(currentDir.toString()));
                break;
            ////Test
            case BIG_FILE_SEND:
                BigFileSend bfs = (BigFileSend) cmd;
//
                if (!Files.exists(currentDir.resolve(bfs.getFileName()))) {
                    Files.createFile(currentDir.resolve(bfs.getFileName()));
                }
                    Files.write(
                            currentDir.resolve(bfs.getFileName()),
                            bfs.getBuff(),
                            StandardOpenOption.APPEND);
                    ctx.writeAndFlush(new ListRequest(currentDir));
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