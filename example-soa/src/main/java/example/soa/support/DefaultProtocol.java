package example.soa.support;

import example.common.Constant;
import example.common.URL;
import example.soa.*;
import example.transport.*;
import example.transport.netty.NettyClient;
import example.transport.netty.NettyServer;
import service.HelloService;
import service.HelloServiceImpl;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by juemingzi on 16/7/12.
 */
public class DefaultProtocol implements Protocol {
    private final ConcurrentMap<String, Invoker> invokers = new ConcurrentHashMap<>();

    private static final ChannelHandler consumerRequestHandler = new AbstractChannelHandler() {
        @Override
        public void receive(Channel channel, Object message) throws Exception {
            if (message instanceof Response) {
                Response response = (Response) message;
                ResponseFuture future = ResponseFuture.getFuture(response.getMsgId());
                future.receive(response);
            }
        }
    };

    private static final Client client = new NettyClient(consumerRequestHandler);

    @Override
    public <T> void export(String interfaceName, Class<T> clazz) throws InterruptedException {
        Invoker invoker = new ServerInvoker(clazz);
        invokers.put(interfaceName, invoker);
        openServer();
    }

    @Override
    public <T> Invoker<T> refer(final Class<T> type, URL url) throws Exception {
        final Channel channel = client.connect(new InetSocketAddress(url.getHost(), url.getPort()));

        return new Invoker<T>() {
            @Override
            public Class<T> getInterface() {
                return type;
            }

            @Override
            public Result invoke(Invocation invocation) throws Exception {

                Request request = new Request();
                request.setInvocation(invocation);

                ResponseFuture responseFuture = new ResponseFuture(request.getMsgId());

                channel.send(request);

                Response response = responseFuture.get();

                return new Result(response.getResult());
            }
        };

    }

    public Invoker getInvoker(String className) {
        return invokers.get(className);
    }

    private void openServer() throws InterruptedException {
        Server server = new NettyServer(requestHandler);
        server.bind(new InetSocketAddress(Constant.LOCAL_HOST, Constant.PORT));
    }

    private final ChannelHandler requestHandler = new AbstractChannelHandler() {
        @Override
        public void receive(Channel channel, Object object) throws Exception {
            if (object instanceof Request) {
                Request request = (Request) object;
                Invocation invocation = request.getInvocation();
                Invoker invoker = getInvoker(invocation.getClazz().getName());

                Result result = invoker.invoke(invocation);

                Response response = new Response();
                response.setMsgId(request.getMsgId());
                response.setResult(result.getData());

                send(channel, response);
            }
        }
    };

    public static void main(String[] args) throws Exception {
        Protocol protocol = new DefaultProtocol();
        protocol.export(HelloService.class.getName(), HelloServiceImpl.class);

    }

}
