package example.registry.zookeeper;

import example.common.ServiceType;
import example.common.URL;
import example.directory.RegistryDirectory;
import example.soa.Invocation;
import example.soa.Invoker;
import example.soa.Protocol;
import example.soa.support.DefaultProtocol;

/**
 * Created by juemingzi on 16/7/12.
 */
public class ConsumerTest {

    public static void main(String[] args) throws Exception {
        ZookeeperRegistry registry = new ZookeeperRegistry();

        Protocol protocol = new DefaultProtocol();
        URL url = new URL(ServiceType.CONSUMER.getLabel(), DemoService.class.getName());
        RegistryDirectory<DemoService> registryDirectory = new RegistryDirectory(protocol, DemoService.class);
        registry.subscribe(url, registryDirectory);

        Invocation invocation = new Invocation();
        invocation.setClazz(DemoService.class);
        invocation.setMethodName("call");
        invocation.setParameterTypes(new Class[]{String.class});
        invocation.setArguments(new Object[]{"hello, ok"});

        for (Invoker<DemoService> invoker : registryDirectory.list(invocation)) {
            System.out.println(invoker.invoke(invocation).getData());
        }


    }


}


