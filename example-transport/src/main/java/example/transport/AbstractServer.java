package example.transport;

/**
 * Created by juemingzi on 16/6/3.
 */
public abstract class AbstractServer implements Server{

    protected final ChannelHandler channelHandler;

    private volatile boolean closed;

    public AbstractServer(ChannelHandler handler){
        this.channelHandler = handler;
    }

    public ChannelHandler getChannelHandler(){
        return channelHandler;
    }

}
