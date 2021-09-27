import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerNIO {
    private static Path dirSever = Paths.get("Server", "root");
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer;

    public ServerNIO() throws IOException {

        buffer = ByteBuffer.allocate(256);
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverChannel.bind(new InetSocketAddress(8189));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (serverChannel.isOpen()) {

            selector.select();

            Set<SelectionKey> keys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    handleAccept(key);
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
                iterator.remove();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ServerNIO();

    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        buffer.clear();
        int read = 0;
        StringBuilder msg = new StringBuilder();
        while (true) {
            if (read == -1) {
                channel.close();
                return;
            }
            read = channel.read(buffer);
            if (read == 0) {
                break;
            }
            buffer.flip();
            while (buffer.hasRemaining()) {
                msg.append((char) buffer.get());
            }
            buffer.clear();
        }
        String message = msg.toString().trim();

       if (message.equals("ls")) {
            channel.write(ByteBuffer.wrap(
                    showServerDir(dirSever).getBytes(StandardCharsets.UTF_8)));
            System.out.println("1");
        } else if (message.startsWith("cat")) {
            channel.write(
                    ByteBuffer.wrap(
                            showFileContent(message.split(" ")[1]).getBytes(StandardCharsets.UTF_8)));

        }

    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        System.out.println("accept");
        channel.register(selector, SelectionKey.OP_READ);
    }

    private static String showServerDir(Path dir) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.list(dir).map(f -> f.getFileName().toString()).forEach(p -> sb.append(p + "\n"));
        return sb.toString();
    }

    public static String showFileContent(String fileName) throws IOException {
        StringBuffer sb = new StringBuffer();
        Path file = Paths.get(dirSever.toString(), fileName);
        Files.readAllLines(file).forEach(s -> sb.append(s));
        return sb.toString();

    }
}

