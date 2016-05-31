package rpc2;

import common.Invoker;
import common.ServerInvoker;
import service.HelloService;
import service.HelloServiceImpl;
import transport.NettyServer;

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

    public static void main(String[] args) throws Exception {
        new NettyServer().start();

        export(HelloService.class.getSimpleName(), HelloServiceImpl.class);

    }


}
