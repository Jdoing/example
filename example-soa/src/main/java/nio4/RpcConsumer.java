package nio4;

import common.Invocation;
import common.Request;
import common.Response;
import service.HelloService;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.channels.SocketChannel;

/**
 * Created by juemingzi on 16/5/24.
 */
public class RpcConsumer {

    private NIOClient client;

    public RpcConsumer() throws IOException {
        client = new NIOClient();
        try {
//            client.initClient();
            client.start();
        } catch (Exception e) {
            client.stopServer();
        }
    }

    public <T> T refer(final Class<?> clazz) throws IOException {
        final SocketChannel channel = client.newSocketChanel();

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invocation = new Invocation();
                invocation.setClazz(clazz);
                invocation.setMethodName(method.getName());
                invocation.setParameterTypes(method.getParameterTypes());
                invocation.setArguments(args);

                Request request = new Request();
                request.setInvocation(invocation);
                client.addResultHolder(request.getMsgId());

                client.send(channel, request);

                Response response = (Response) client.getResult(request.getMsgId());

                return response.getResult();
            }
        });

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        RpcConsumer rpcConsumer = new RpcConsumer();

//        System.out.println(Thread.currentThread().getName() + " main thread");

        HelloService helloService = rpcConsumer.refer(HelloService.class);
        String result = helloService.hello("Foo");

        System.out.println(result);
//        System.out.println(rpcConsumer.getResult());

//        System.out.println(helloService.hello("Foo"));

        result = helloService.hello("Foo");
        System.out.println(result);

        System.out.println("end");
    }


}
