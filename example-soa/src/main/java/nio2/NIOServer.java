package nio2;

import example.soa.Invocation;
import example.common.Constant;

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by juemingzi on 16/5/21.
 */
public class NIOServer extends Thread {
    private Selector selector;

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

    private void processKey(SelectionKey key) throws IOException {
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
        System.out.println(Thread.currentThread().getName() + " accept connection...");

        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);

        channel.register(selector, SelectionKey.OP_READ);

    }

    private void doRead(SelectionKey key) throws IOException {
        System.out.println(Thread.currentThread().getName() + " read data from client...");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(Constant.BUFFER_SIZE);

        try {
            buffer.clear();
            if (channel.read(buffer) > 0) {
                buffer.flip();
                //序列化
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
                try {
                    Invocation invocation = (Invocation) objectInputStream.readObject();
                    //RPC调用在这里实现
                    Class<?> clazz = invocation.getClazz();
                    Object object = clazz.newInstance();
                    Method method = clazz.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    Object result = method.invoke(object, invocation.getArguments());

                    channel.register(selector, SelectionKey.OP_WRITE, result);//发送响应
                } finally {
                    objectInputStream.close();
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();

            key.cancel();
        }
    }

    private void doWrite(SelectionKey key) throws IOException {
        System.out.println(Thread.currentThread().getName() + " write data to client...");
        SocketChannel channel = (SocketChannel) key.channel();

        Object object = key.attachment();
        if (object != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            try {
                objectOutput.writeObject(object);
                objectOutput.flush();

                ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

                while (byteBuffer.hasRemaining()) {
                    channel.write(byteBuffer);
                }
                channel.register(selector, SelectionKey.OP_READ);
            } finally {
                objectOutput.close();
                byteArrayOutputStream.close();
            }
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
