package nio4;

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
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by juemingzi on 16/5/21.
 */
public class NIOClient extends Thread {
    private Selector selector;

    private Map<Long, Response> resMap = new ConcurrentHashMap<>();

    private Map<Long, ResultHolder> resultHolderMap = new ConcurrentHashMap<>();

    private Queue<Task> tasks = new ConcurrentLinkedQueue<>();

    private static class Task {
        public static final int REGISTER = 1;
        public static final int CHANGEOPS = 2;

        public SocketChannel channel;
        public int type;
        public int ops;
        public Object data;

        public Task(SocketChannel channel, int type, int ops, Object data) {
            this.channel = channel;
            this.type = type;
            this.ops = ops;
            this.data = data;
        }
    }

    private static class ResultHolder {
        Lock lock = new ReentrantLock();
        Condition done = lock.newCondition();
    }

    public NIOClient() throws IOException {
        selector = Selector.open();
    }

    public void initClient() throws IOException {
        selector = Selector.open();
    }

    public void addResultHolder(long msgId) {
        resultHolderMap.put(msgId, new ResultHolder());
    }

    public SocketChannel newSocketChanel() throws IOException {
        InetSocketAddress address = new InetSocketAddress(Constant.PORT);

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);

        channel.connect(address);

        return channel;
    }

    public void send(SocketChannel channel, Object object) throws InterruptedException {
        if (channel.isConnected()) {
            System.out.println("add write task");
            tasks.add(new Task(channel, Task.CHANGEOPS, SelectionKey.OP_WRITE, object));
        } else {
            System.out.println("add connect task");
            tasks.add(new Task(channel, Task.REGISTER, SelectionKey.OP_CONNECT, object));
        }

        selector.wakeup();
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " 客户端已经启动!");
        try {
            while (true) {

                if (tasks.peek() != null) {
                    Task task = tasks.remove();
                    switch (task.type) {
                        case Task.CHANGEOPS:
                            SelectionKey key = task.channel.keyFor(selector);
                            key.interestOps(task.ops);
                            key.attach(task.data);
                            break;
                        case Task.REGISTER:
                            SelectionKey key2 = task.channel.register(selector, task.ops);
                            key2.attach(task.data);
                            break;
                        default:
                            throw new IllegalArgumentException("task.type error");
                    }
                }

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
                            key.interestOps(SelectionKey.OP_WRITE);
//                            channel.configureBlocking(false);
//                            channel.register(selector, SelectionKey.OP_WRITE);
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
        System.out.print("read data from server====>");
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

            ResultHolder resultHolder = resultHolderMap.get(response.getMsgId());
            resultHolder.lock.lock();
            try {
                resMap.put(response.getMsgId(), response);
                resultHolder.done.signal();
            } finally {
                resultHolder.lock.unlock();
            }

            //不要随便设置OP_WRITE,否则会耗尽CPU,只有在需要的时候才设置
//            channel.register(selector, SelectionKey.OP_WRITE);
        } else {
            System.out.println("no data to read!");
        }
    }

    public Object getResult(long msgId) throws InterruptedException {
        Response response = resMap.get(msgId);
        if (response == null) {
            ResultHolder resultHolder = resultHolderMap.get(msgId);

            System.out.println(Thread.currentThread().getName() + " get result");

            resultHolder.lock.lock();
            try {
                while ((response = resMap.get(msgId)) == null) {
                    resultHolder.done.await();
                }

            } finally {
                resultHolder.lock.unlock();
            }
        }

        return resMap.remove(msgId);
    }

    private void doWrite(SelectionKey key) throws Exception {
        Object data = key.attachment();
        if (data == null) {
            System.out.println("no data to write");
            return;
        }

        System.out.print("send data to server===>");
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
