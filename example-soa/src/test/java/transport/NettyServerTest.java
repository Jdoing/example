package transport;

import common.Request;
import example.transport.netty.DecoderHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;
import util.NIOUtil;

/**
 * Created by juemingzi on 16/5/28.
 */
public class NettyServerTest {

    @Test
    public void testRequest() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new DecoderHandler(), new NettyHandler());

        Request request = new Request();
        request.setMsgId(11);

        byte[] bytes = NIOUtil.getByteArray(request);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);

        channel.writeInbound(byteBuf);
        Object out =  channel.readOutbound();
        System.out.println(out);
    }

}