package example.transport.netty;

import example.transport.*;
import example.transport.Channel;
import example.transport.ChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by juemingzi on 16/6/4.
 */
public class NettyClient implements Client{

    private Bootstrap bootstrap;

    private NioEventLoopGroup workerGroup;

    private ChannelHandler channelHandler;

    public NettyClient(ChannelHandler channelHandler){
        this.channelHandler = channelHandler;
        init();
    }

    private void init(){
        workerGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();

        bootstrap.group(workerGroup); // (2)
        bootstrap.channel(NioSocketChannel.class); // (3)
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DecoderHandler());
                ch.pipeline().addLast(new NettyClientHandler(channelHandler));
                ch.pipeline().addLast(new EncoderHandler());
            }
        });
    }


    @Override
    public Channel connect(InetSocketAddress inetSocketAddress) throws Exception {
        ChannelFuture f = bootstrap.connect(inetSocketAddress).sync();

        io.netty.channel.Channel channel = f.channel();

        return new NettyChannel(channel, channelHandler);
    }
}
