package example.transport.netty;

import example.transport.Channel;
import example.transport.ChannelHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by juemingzi on 16/6/3.
 */
public class NettyChannel implements Channel {

    private static final ConcurrentHashMap<io.netty.channel.Channel, NettyChannel> channelMap = new ConcurrentHashMap<>();

    //包装netty的client,用到了适配器模式
    private io.netty.channel.Channel channel;

    private ChannelHandler channelHandler;

    public NettyChannel(io.netty.channel.Channel channel, ChannelHandler channelHandler) {
        this.channel = channel;
        this.channelHandler = channelHandler;
    }

    static NettyChannel getOrAddChannel(io.netty.channel.Channel channel, ChannelHandler handler) {

        NettyChannel ret = channelMap.get(channel);

        //不存在则尝试更新
        if (ret == null) {
            NettyChannel nettyChannel = new NettyChannel(channel, handler);

            //这里处于线程安全考虑,如果存在则更新,直接返回
            ret = channelMap.putIfAbsent(channel, nettyChannel);
            if (ret == null) {
                ret = nettyChannel;
            }
        }

        return ret;
    }

    static void removeIfInactive(io.netty.channel.Channel channel) {
        if (channel != null && !channel.isActive()) {
            channelMap.remove(channel);
        }

    }

    @Override
    public void send(Object message) {
        this.channel.writeAndFlush(message);
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public boolean isConnnect() {
        return false;
    }

}
