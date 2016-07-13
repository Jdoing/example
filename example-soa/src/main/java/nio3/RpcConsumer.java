package nio3;

import example.soa.Invocation;
import example.soa.Request;
import example.soa.Response;
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

        HelloService helloService = rpcConsumer.refer(HelloService.class);
        String result = helloService.hello("Foo");

        System.out.println(result);
    }
}
