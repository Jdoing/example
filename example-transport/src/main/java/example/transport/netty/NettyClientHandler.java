package example.transport.netty;

import example.transport.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by juemingzi on 16/6/4.
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandler channelHandler;

    public NettyClientHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel.getOrAddChannel(ctx.channel(), channelHandler);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getOrAddChannel(ctx.channel(), channelHandler);

        channelHandler.receive(nettyChannel, object);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
