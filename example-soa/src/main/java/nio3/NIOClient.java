package nio3;

import common.Constant;
import common.Response;
import util.NIOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by juemingzi on 16/5/21.
 */
public class NIOClient extends Thread {
    private Selector selector;

    private Map<Long, Response> resMap = new ConcurrentHashMap<Long, Response>();

    private Map<Long, Lock> resultHolderMap = new ConcurrentHashMap<Long, Lock>();

    public void initClient() throws IOException {
        System.out.println(Thread.currentThread().getName() + " init client");
        selector = Selector.open();
    }

    public void addResultHolder(long msgId) {
        Lock lock = new ReentrantLock();

        resultHolderMap.put(msgId, lock);
    }

    public SelectionKey newSelectionKey() throws IOException {
        InetSocketAddress address = new InetSocketAddress(Constant.PORT);

        selector.wakeup();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);

        SelectionKey key = channel.register(selector, SelectionKey.OP_CONNECT);
        channel.connect(address);

        return key;
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " 客户端已经启动!");
        try {
            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (key.isConnectable()) {
                            System.out.println(Thread.currentThread().getName() + " 连接成功!!");

                            SocketChannel channel = (SocketChannel) key.channel();
                            // 如果正在连接，则完成连接
                            if (channel.isConnectionPending()) {
                                channel.finishConnect();
                            }

//                            channel.configureBlocking(false);
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

    public Response getResponse(String msgId) {
        if (resMap.containsKey(msgId)) {
            return resMap.remove(msgId);
        } else {
            return null;
        }
    }

    private void doRead(SelectionKey key) throws IOException, ClassNotFoundException {
        System.out.println("read data from server...");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(Constant.BUFFER_SIZE);

        if (channel.read(buffer) > 0) {

            Object object = NIOUtil.getObject(buffer);
            Response response;
            if (object instanceof Response) {
                response = (Response) object;
            } else {
                throw new ClassCastException("object不能转换为result");
            }

            resMap.put(response.getMsgId(), response);

            Lock lock = resultHolderMap.get(response.getMsgId());
            lock.notifyAll();

            System.out.println(response.getResult());
            channel.register(selector, SelectionKey.OP_WRITE);

        }
    }

    //TODO 返回后需要删除response
    public Object getResult(long msgId) throws InterruptedException {
        Response response = resMap.get(msgId);
        if (response == null) {
            Lock lock = resultHolderMap.get(msgId);

            System.out.println(Thread.currentThread().getName() + " get result");

            synchronized (lock){
                while ((response = resMap.get(msgId)) == null) {
                    lock.wait();
                }

                resMap.put(msgId, response);
            }

//            lock.lock();
//            try {
//                while ((response = resMap.get(msgId)) == null) {
//                    lock.wait();
//                }
//
//                resMap.put(msgId, response);
//
//            } finally {
//                lock.unlock();
//            }
        }

        return response;
    }

    private void doWrite(SelectionKey key) throws Exception {
        Object data = key.attachment();
        if (data == null) {
            return;
        }

        System.out.println("write data to server...");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = NIOUtil.getByteBuffer(data);
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
            client.initClient();

            client.start();
        } catch (Exception e) {
            client.stopServer();
        }
    }

}
