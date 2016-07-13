package example.registry.zookeeper;


import example.common.ServiceType;
import example.common.URL;
import example.directory.RegistryDirectory;
import example.soa.support.DefaultProtocol;
import org.junit.Test;

/**
 * Created by juemingzi on 16/6/13.
 */
public class ZookeeperRegistryTest {

    private ZookeeperRegistry registry = new ZookeeperRegistry();

    @Test
    public void testRegister() throws Exception {
        URL url = new URL(ServiceType.PROVIDER.getLabel(), DemoService.class.getName());
        registry.register(url);
        new DefaultProtocol().export(DemoService.class.getName(), DemoServiceImp.class);
    }

    @Test
    public void testSubcribe() throws Exception {
        URL url = new URL(ServiceType.CONSUMER.getLabel(), DemoService.class.getName());
        RegistryDirectory registryDirectory = new RegistryDirectory(new DefaultProtocol(), DemoService.class);
        registry.subscribe(url, registryDirectory);
    }


}
