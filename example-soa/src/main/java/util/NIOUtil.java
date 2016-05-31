package util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by juemingzi on 16/5/21.
 */
public class NIOUtil {
    public static final int BUFFER_SIZE = 50;

    public static String getString(ByteBuffer buffer) {
        Charset charset;
//        CharsetDecoder decoder;
        CharBuffer charBuffer;
        try {
            charset = Charset.forName("utf-8");
//            decoder = charset.newDecoder();
            // charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空
            charBuffer = charset.decode(buffer.asReadOnlyBuffer());
            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static void readBuffer(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        if (!channel.isOpen())
            return;

        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        int num = channel.read(readBuffer);
        readBuffer.flip();
        if (num > 0) {
            System.out.println(getString(readBuffer));
        } else {
            System.out.println("没有数据");
        }

//        channel.close();
    }

    public static void writeBuffer(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        if (!channel.isOpen())
            return;

        ByteBuffer writeBuffer = ByteBuffer.wrap(new byte[]{'a'});
//        writeBuffer.flip();
        int num = channel.write(writeBuffer);

        if (num > 0) {
            System.out.println("写入成功");
        } else {
            System.out.println("没有数据");
        }

//        channel.close();
    }

    public static ByteBuffer getByteBuffer(Object object) throws Exception {
        return ByteBuffer.wrap(getByteArray(object));
    }

    public static byte[] getByteArray(Object object) throws IOException {
        if (object == null) {
            throw new IllegalArgumentException("object不能为空");
        }

        if (!(object instanceof Serializable)) {
            throw new IOException("object没有实现Serializable,不能序列化");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(byteArrayOutputStream);
        try {
            objectOutput.writeObject(object);
            objectOutput.flush();

           return byteArrayOutputStream.toByteArray();
        } finally {
            objectOutput.close();
            byteArrayOutputStream.close();
        }
    }

    public static Object getObject(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException {
        InputStream inputStream = new ByteArrayInputStream(byteBuffer.array());
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            try{
                Object object = objectInputStream.readObject();
                return object;
            }finally {
                objectInputStream.close();
            }

        }finally {
            inputStream.close();
        }
    }

}
