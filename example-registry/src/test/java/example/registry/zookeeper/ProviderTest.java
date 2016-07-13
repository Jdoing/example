package example.registry.zookeeper;

import example.common.ServiceType;
import example.common.URL;
import example.soa.support.DefaultProtocol;

/**
 * Created by juemingzi on 16/7/12.
 */
public class ProviderTest {

    public static void main(String[] args) throws Exception {
        ZookeeperRegistry registry = new ZookeeperRegistry();
        URL url = new URL(ServiceType.PROVIDER.getLabel(), DemoService.class.getName());
        registry.register(url);

        new DefaultProtocol().export(DemoService.class.getName(), DemoServiceImp.class);

        System.out.println("register finish");
    }

}
