package example.transport;

/**
 * Created by juemingzi on 16/6/3.
 */
public interface Endpoint {

    ChannelHandler getChannelHandler();

    void close();

    boolean isClosed();
}
