package example.registry.zookeeper;

import example.registry.Registry;
import example.registry.demo.DemoService;

/**
 * Created by juemingzi on 16/6/12.
 */
public class ZookeeperRegistry implements Registry{

    private ZookeeperClient zookeeperClient;

    public ZookeeperRegistry(){
        zookeeperClient = new ZookeeperClient();
    }

    @Override
    public void register(String path) throws Exception {
        zookeeperClient.create(path);
    }

    @Override
    public void subscribe(String path) throws Exception {


    }

    public static void main(String[] args) throws Exception {
        String path = '/' + DemoService.class.getPackage().getName() + '.' + DemoService.class.getSimpleName();
        System.out.println("path: " + path);

        ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry();

        zookeeperRegistry.register(path);

    }


}
