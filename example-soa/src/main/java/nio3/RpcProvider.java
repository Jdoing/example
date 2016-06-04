package nio3;

import common.Constant;
import service.HelloService;
import service.HelloServiceImpl;

import java.io.IOException;

/**
 * Created by juemingzi on 16/5/24.
 */
public class RpcProvider {

    private NIOServer server;

    public RpcProvider() throws IOException {
        server = new NIOServer();
        try {
            server.initServer(Constant.PORT);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            server.stopServer();
        }

    }

    public void export(Class<?> service, Class clazz) throws InstantiationException, IllegalAccessException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz不能为空!");
        }

        if (server.containInvoker(service)) {
            return;
        } else {
            ServerInvoker invoker = new ServerInvoker(clazz);
            server.addInvoker(service, invoker);
        }
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException {
        RpcProvider rpcProvider = new RpcProvider();

        rpcProvider.export(HelloService.class, HelloServiceImpl.class);
    }
}
