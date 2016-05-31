package nio3;

import common.Invocation;
import common.Request;
import service.HelloService;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.channels.SelectionKey;

/**
 * Created by juemingzi on 16/5/24.
 */
public class RpcConsumer {

    private NIOClient client;

    public RpcConsumer() throws IOException {
        client = new NIOClient();
        try {
            client.initClient();

            client.start();

        } catch (Exception e) {
            client.stopServer();
        }
    }

    public <T> T refer(final Class<?> clazz) throws IOException {
        final SelectionKey selectionKey = client.newSelectionKey();

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

                selectionKey.attach(request);

                return client.getResult(request.getMsgId());
            }
        });

    }

    public Object getResult() throws IOException, InterruptedException {
        final SelectionKey selectionKey = client.newSelectionKey();
        Invocation invocation = new Invocation();
        invocation.setClazz(HelloService.class);
        invocation.setMethodName("hello");
        invocation.setParameterTypes(new Class[]{String.class});
        invocation.setArguments(new Object[]{"Foo"});

        Request request = new Request();
        request.setInvocation(invocation);

        client.addResultHolder(request.getMsgId());

        selectionKey.attach(request);

        Thread.sleep(10000);
//        return client.getResult(request.getMsgId());
        return "TTT";

    }


    public static void main(String[] args) throws IOException, InterruptedException {
        RpcConsumer rpcConsumer = new RpcConsumer();

        System.out.println(Thread.currentThread().getName() + " main thread");

        HelloService helloService = rpcConsumer.refer(HelloService.class);
        System.out.println(helloService.hello("Foo"));
//        System.out.println(rpcConsumer.getResult());

    }


}
