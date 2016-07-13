package rpc2;

import example.soa.ClientInvoker;
import example.soa.Invocation;
import example.soa.Response;
import example.soa.ResponseFuture;
import service.HelloService;
import transport.ExchangeClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by juemingzi on 16/5/31.
 */
public class RpcConsumer {


    public static void receive(Response response) {
        ResponseFuture future = ResponseFuture.getFuture(response.getMsgId());
        future.receive(response);
    }

    public static <T> T refer(final Class<T> clazz) throws Exception {
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

                Response response = (Response) clientInvoker.invoke(invocation).getData();
                return response.getResult();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        HelloService helloService = refer(HelloService.class);

        String string = helloService.hello("Foo");

        System.out.println(string);
    }

}
