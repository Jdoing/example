package example.transport;

import java.net.InetSocketAddress;

/**
 * Created by juemingzi on 16/6/3.
 */
public interface Server extends Endpoint{

    void bind(InetSocketAddress address) throws InterruptedException;

}
