package rpc;

import service.HelloService;
import service.HelloServiceImpl;

/**
 * Created by juemingzi on 16/5/16.
 */
public class RpcProvider {
    public static void main(String[] args) throws Exception {
        HelloService service = new HelloServiceImpl();
        RpcFramework.export(service, 1234);
    }
}
