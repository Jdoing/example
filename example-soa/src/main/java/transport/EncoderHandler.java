package transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by juemingzi on 16/5/27.
 */
public class EncoderHandler extends MessageToByteEncoder<Object>{


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
        ){
            objectOutputStream.writeObject(msg);

            out.writeBytes(byteArrayOutputStream.toByteArray());
        }

    }
}
