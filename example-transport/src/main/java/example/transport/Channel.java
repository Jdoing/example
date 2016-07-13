package example.transport;

import java.net.InetSocketAddress;

/**
 * Created by juemingzi on 16/6/3.
 */
public interface Channel extends Endpoint{

    ChannelHandler getChannelHandler();

    InetSocketAddress getRemoteAddress();

    boolean isConnnect();

    void send(Object message);

}
