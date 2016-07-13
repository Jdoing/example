package transport;

import example.soa.Invocation;
import example.soa.Request;
import example.soa.ResponseFuture;
import io.netty.channel.Channel;

import java.util.concurrent.ExecutionException;

/**
 * Created by juemingzi on 16/5/28.
 */
public class ExchangeClient {

    private NettyClient client;

    private Channel channel;

    public ExchangeClient() throws Exception {
        client = new NettyClient();
        channel = client.newChannel();
    }

    public ResponseFuture send(Invocation data) throws ExecutionException, InterruptedException {
        Request request = new Request();
        request.setInvocation(data);

        channel.writeAndFlush(request);

        return new ResponseFuture(request.getMsgId());
    }


}
