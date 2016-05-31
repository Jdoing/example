package nio4;

import common.Invocation;
import common.Request;
import service.HelloService;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by juemingzi on 16/5/24.
 */
public class RpcManager {


    public static void main(String[] args) throws IOException, InterruptedException {
        NIOClient client = new NIOClient();
        client.start();

        Invocation invocation = new Invocation();
        invocation.setClazz(HelloService.class);
        invocation.setMethodName("hello");
        invocation.setParameterTypes(new Class[]{String.class});
        invocation.setArguments(new Object[]{"Foo"});

        Request request = new Request();
        request.setInvocation(invocation);

        SocketChannel socketChannel = client.newSocketChanel();
        client.send(socketChannel, request);

        Thread.sleep(5000);
    }


}
