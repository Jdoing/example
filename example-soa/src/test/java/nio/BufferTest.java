package nio;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

/**
 * Created by juemingzi on 16/5/19.
 */
public class BufferTest {

    @Test
    public void test(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put((byte) 'a');
        byteBuffer.put((byte) 'b');
        byteBuffer.put((byte) 'c');

        Channel channel = Channels.newChannel(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });

        CharBuffer charBuffer = CharBuffer.allocate(10);
        charBuffer.put('a');
        charBuffer.put('b');
        charBuffer.put('c');

        System.out.println(charBuffer.get());
    }

    @Test
    public void testChannel() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("read.txt");
        FileChannel fileChannel = fileInputStream.getChannel();
        ByteBuffer readBuffer = ByteBuffer.allocate(10);

        fileChannel.read(readBuffer);

    }

    @Test
    public void testWrite() throws IOException {
//        FileOutputStream fileOutputStream = new FileOutputStream("tmp.txt");
//        FileChannel fileChannel = fileOutputStream.getChannel();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
//
//        String txt = "I am test";
//        for(int i = 0; i < txt.length(); i++){
//            byteBuffer.putChar(txt.charAt(i));
//        }
//
//        byteBuffer.flip();
//        fileChannel.write(byteBuffer);
//        fileOutputStream.close();
//        byteBuffer.clear();

        ByteBuffer buffer = ByteBuffer.allocate(18);
        FileInputStream fi = new FileInputStream("tmp.txt");
        FileChannel readChannel = fi.getChannel();
        readChannel.read(buffer);
        buffer.flip();

        for(int i = 0; buffer.hasRemaining(); i++){
            System.out.println(buffer.getChar());
        }

    }


}
