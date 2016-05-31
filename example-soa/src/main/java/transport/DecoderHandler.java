package transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * Created by juemingzi on 16/5/27.
 */
public class DecoderHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            in.clear();
            Object object =  objectInputStream.readObject();
            out.add(object);
        }
    }
}
