package nio4;

import common.*;
import util.NIOUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by juemingzi on 16/5/21.
 */
public class NIOServer extends Thread {
    private Selector selector;

    private ServerSocketChannel ssc;

    private int port;

    private ConcurrentHashMap<Class<?>, ClientInvoker> invokerMap = new ConcurrentHashMap<Class<?>, ClientInvoker>();

    public NIOServer() {
    }

    public NIOServer(int port) throws IOException {
        this.port = port;
    }

    public boolean containInvoker(Class<?> clazz) {
        return invokerMap.containsKey(clazz);
    }

    public void addInvoker(Class<?> clazz, ClientInvoker invoker) {
        invokerMap.put(clazz, invoker);
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
                        processKey(key);
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
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (selector != null) {
                selector.close();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void processKey(SelectionKey key) throws Exception {
        if (key.isAcceptable()) {
            doAccept(key);
        }

        if (key.isReadable()) {
            doRead(key);
        }

        if (key.isWritable()) {
            doWrite(key);
        }
    }

    private void doAccept(SelectionKey key) throws IOException {
        System.out.println("accept connection...");

        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);

        channel.register(selector, SelectionKey.OP_READ);

    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(Constant.BUFFER_SIZE);

        try {
            buffer.clear();
            if (channel.read(buffer) > 0) {
                System.out.println("read data from client...");
                buffer.flip();

                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
                try {

                    Request request = (Request) objectInputStream.readObject();

                    Invocation invocation = request.getInvocation();

                    Class<?> clazz = invocation.getClazz();
                    if (invokerMap.containsKey(clazz)) {
                        ClientInvoker invoker = invokerMap.get(clazz);
                        Object result = invoker.invoke(invocation);

                        Response response = new Response();
                        response.setResult(result);
                        response.setMsgId(request.getMsgId());

                        channel.register(selector, SelectionKey.OP_WRITE, response);
                    }
                } finally {
                    objectInputStream.close();
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();

            key.cancel();
        }
    }

    private void doWrite(SelectionKey key) throws Exception {
        Object data = key.attachment();
        if (data == null) {
            return;
        }

        System.out.println("write data to client...");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = NIOUtil.getByteBuffer(data);
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        channel.register(selector, SelectionKey.OP_READ);
    }

    public void initServer(int port) throws IOException {
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(port));
        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
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
