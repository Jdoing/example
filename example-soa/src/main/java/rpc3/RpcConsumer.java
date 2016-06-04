package rpc3;

import common.*;
import example.transport.AbstractChannelHandler;
import example.transport.Channel;
import example.transport.ChannelHandler;
import example.transport.Client;
import example.transport.netty.NettyClient;
import service.HelloService;
import transport.ExchangeClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * Created by juemingzi on 16/5/31.
 */
public class RpcConsumer {

    private static final ChannelHandler requestHandler = new AbstractChannelHandler() {
        @Override
        public void receive(Channel channel, Object message) throws Exception {
            if (message instanceof Response) {
                Response response = (Response) message;
                ResponseFuture future = ResponseFuture.getFuture(response.getMsgId());
                future.receive(response);
            }
        }
    };

    private static final Client client = new NettyClient(requestHandler);

    public static <T> T refer(final Class<T> clazz) throws Exception {

        final Channel channel = client.connect(new InetSocketAddress(Constant.LOCAL_HOST, Constant.PORT));

        ExchangeClient exchangeClient = new ExchangeClient();
        final ClientInvoker clientInvoker = new ClientInvoker(clazz);
        clientInvoker.setExchangeClient(exchangeClient);

        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invocation = new Invocation();
                invocation.setClazz(clazz);
                invocation.setMethodName(method.getName());
                invocation.setParameterTypes(method.getParameterTypes());
                invocation.setArguments(args);

                Request request = new Request();
                request.setInvocation(invocation);

                ResponseFuture responseFuture = new ResponseFuture(request.getMsgId());

                channel.send(request);

                Response response = responseFuture.get();

                return response.getResult();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        HelloService helloService = refer(HelloService.class);

        for (int i = 0; i < 100; i++) {
            String string = helloService.hello("Foo");

            System.out.println(string);

            Thread.sleep(1000);
        }

    }

}
