package transport;

import common.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by juemingzi on 16/5/26.
 */
public class NettyClient {
    private final String host;
    private final int port;
    private Bootstrap bootstrap;

    public NettyClient() throws Exception {
        this(Constant.LOCAL_HOST, Constant.PORT);
    }

    public NettyClient(String host, int port) throws Exception {
        this.host = host;
        this.port = port;

        start();
    }

    private void start() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap(); // (1)
            bootstrap.group(workerGroup); // (2)
            bootstrap.channel(NioSocketChannel.class); // (3)
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DecoderHandler());
                    ch.pipeline().addLast(new NettyHandler());
                    ch.pipeline().addLast(new EncoderHandler());
                }
            });

//            // Start the client.
//            ChannelFuture f = bootstrap.connect(host, port).sync(); // (5)
//
//            // Wait until the connection is closed.
//            f.channel().closeFuture().sync();

    }

    public Channel newChannel() throws InterruptedException {
        ChannelFuture f = bootstrap.connect(host, port).sync();

        Channel channel = f.channel();
        return channel;
    }


    public static void main(String[] args) throws Exception {
        new NettyClient("localhost", 65535);
    }
}
