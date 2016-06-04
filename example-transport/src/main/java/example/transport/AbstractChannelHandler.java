package example.transport;

/**
 * Created by juemingzi on 16/6/4.
 */
public abstract class AbstractChannelHandler implements ChannelHandler {

    @Override
    public void send(Channel channel, Object object) {
        channel.send(object);
    }

}
