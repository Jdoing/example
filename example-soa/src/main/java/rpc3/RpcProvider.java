package rpc3;

import example.common.Constant;
import example.soa.*;
import example.transport.AbstractChannelHandler;
import example.transport.Channel;
import example.transport.ChannelHandler;
import example.transport.Server;
import example.transport.netty.NettyServer;
import service.HelloService;
import service.HelloServiceImpl;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by juemingzi on 16/5/30.
 */
public class RpcProvider {

    private static final Map<String, Invoker> invokers = new ConcurrentHashMap<>();

    public static Invoker getInvoker(String className) {
        return invokers.get(className);
    }

    public static void export(String interfaceName, Class<?> clazz) {
        Invoker invoker = new ServerInvoker(clazz);
        invokers.put(interfaceName, invoker);
    }

    private static final ChannelHandler requestHandler = new AbstractChannelHandler() {
        @Override
        public void receive(Channel channel, Object object) throws Exception {
            if (object instanceof Request) {
                Request request = (Request) object;

                Invocation invocation = request.getInvocation();

                Invoker invoker = getInvoker(invocation.getClazz().getSimpleName());

                Result result = invoker.invoke(invocation);

                Response response = new Response();
                response.setMsgId(request.getMsgId());
                response.setResult(result.getData());

                send(channel, response);
            }
        }
    };

    public static void main(String[] args) throws Exception {
        Server server = new NettyServer(requestHandler);
        server.bind(new InetSocketAddress(Constant.LOCAL_HOST, Constant.PORT));

        export(HelloService.class.getSimpleName(), HelloServiceImpl.class);

        System.out.println("export finish!");
    }


}
