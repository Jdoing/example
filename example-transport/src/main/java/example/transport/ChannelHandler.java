package example.transport;

/**
 * Created by juemingzi on 16/6/3.
 */
public interface ChannelHandler {

    //带上Channel,做成无状态化设计,有利保证线程安全
    void send(Channel channel, Object message);

    void receive(Channel channel, Object message) throws Exception;

}
