package example.transport;

import java.net.InetSocketAddress;

/**
 * Created by juemingzi on 16/6/3.
 */
public interface Client {
    Channel connect(InetSocketAddress inetSocketAddress) throws Exception;
}
