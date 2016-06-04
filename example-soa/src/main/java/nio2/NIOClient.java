package nio2;

import common.Invocation;
import service.HelloServiceImpl;
import common.Constant;
import util.NIOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by juemingzi on 16/5/21.
 */
public class NIOClient extends Thread {
    private Selector selector;

    private void initClient(int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(port);

        selector = Selector.open();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);

        channel.register(selector, SelectionKey.OP_CONNECT);
        channel.connect(address);

    }

    public void run() {
        System.out.println("客户端已经启动!");
        try {
            while (selector.select() > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (key.isConnectable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            // 如果正在连接，则完成连接
                            if (channel.isConnectionPending()) {
                                channel.finishConnect();
                            }
                            channel.register(selector, SelectionKey.OP_WRITE);
                        }

                        if (key.isReadable()) {
                            doRead(key);
                        }

                        if (key.isWritable()) {
                            doWrite(key);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doRead(SelectionKey key) throws IOException, ClassNotFoundException {
        System.out.println("read data from server...");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(Constant.BUFFER_SIZE);

        if (channel.read(buffer) > 0) {

            Object object = NIOUtil.getObject(buffer);

            System.out.println(object);
            channel.register(selector, SelectionKey.OP_WRITE);
        }
    }

    private void doWrite(SelectionKey key) throws Exception {
        System.out.println("write data to server...");
        SocketChannel channel = (SocketChannel) key.channel();

        //传递类名,方法名,方法参数
        Invocation invocation = new Invocation();
        invocation.setClazz(HelloServiceImpl.class);
        invocation.setMethodName("hello");
        invocation.setParameterTypes(new Class[]{String.class});
        invocation.setArguments(new Object[]{"foo"});

        ByteBuffer buffer = NIOUtil.getByteBuffer(invocation);
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        channel.register(selector, SelectionKey.OP_READ);
    }

    public void stopServer() throws IOException {
        if (selector != null && selector.isOpen()) {
            selector.close();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        NIOClient client = new NIOClient();
        try {
            client.initClient(8859);

            client.start();
        } catch (Exception e) {
            client.stopServer();
        }
    }

}
