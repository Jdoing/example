package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import static util.NIOUtil.getString;

/**
 * Created by juemingzi on 16/5/21.
 */
public class NIOServer extends Thread {
    private Selector selector;
    private int port;

    public NIOServer() {
    }

    public NIOServer(int port) throws IOException {
        this.port = port;
    }

    public void run() {
        System.out.println("服务端线程已经启动!");
        try {
            while (selector.select() > 0) {
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    try {
                        if (key.isAcceptable()) {
                            doAccept(key);
                        }

                        if (key.isReadable()) {
                            doRead(key);
                        }

                        if (key.isWritable()) {
                            doWrite(key);
                        }
                    } catch (ClosedSelectorException cek) {
                        cek.printStackTrace();
                    } catch (CancelledKeyException ck) {
                        ck.printStackTrace();
                        key.cancel();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(selector != null){
                selector.close();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void doAccept(SelectionKey key) throws IOException {
        System.out.println("accept...");

        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);

//                            channel.write(ByteBuffer.wrap(new String("向客户端发送了一条信息").getBytes()));
        channel.register(selector, SelectionKey.OP_READ);

    }

    private void doRead(SelectionKey key) throws IOException {
        System.out.println("read data from client...");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            while (channel.read(buffer) > 0) {
                buffer.flip();
                System.out.println(getString(buffer));
                buffer.clear();
            }

            channel.register(selector, SelectionKey.OP_WRITE);
        } catch (Throwable e) {
            e.printStackTrace();

            key.cancel();
        }
    }

    private void doWrite(SelectionKey key) throws IOException {
        System.out.println("write data to client...");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(new String("Hi, client").getBytes("UTF-8"));
        try {
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }

            channel.register(selector, SelectionKey.OP_READ);
        } catch (Throwable e) {
            e.printStackTrace();
            key.cancel();
        }
    }

    public void initServer(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void stopServer() throws IOException {
        if (!selector.isOpen()) {
            selector.close();
        }
    }

    public static void main(String[] args) throws IOException {
        NIOServer server = new NIOServer();
        try {
            server.initServer(8859);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            server.stopServer();
        }
    }


}
