package example.directory;

import example.common.URL;
import example.registry.NotifyListener;
import example.registry.Registry;
import example.soa.Invocation;
import example.soa.Invoker;
import example.soa.Protocol;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by juemingzi on 16/7/8.
 */
public class RegistryDirectory<T> implements Directory<T>, NotifyListener {
    private Registry registry;

    private Protocol protocol;

    private Class<T> serviceType;

    //缓存已经refer的Invoker，只有在注册中心推送的时候变更
    // Map<url, Invoker> cache service url to invoker mapping.
    private volatile Map<String, Invoker<T>> urlInvokerMap = new HashMap<>(); // 初始为null以及中途可能被赋为null，请使用局部变量引用

    public RegistryDirectory(Protocol protocol, Class<T> serviceType){
        this.protocol = protocol;
        this.serviceType = serviceType;
    }

    @Override
    public List<Invoker<T>> list(Invocation invocation) {
        List<Invoker<T>> invokers = new ArrayList<>();
        for (String path : urlInvokerMap.keySet()){
            if(StringUtils.contains(path, invocation.getClazz().getName())){
                invokers.add(urlInvokerMap.get(path));
            }
        }

        return invokers;
    }

    @Override
    public void notify(List<URL> urls) throws Exception {
        Map<String, Invoker<T>> invokerMap = new HashMap<>();
        for(URL url : urls){
            Invoker invoker = urlInvokerMap.get(url.getPath());
            if(invoker == null){
                System.out.println("重新refer");
                invokerMap.put(url.getPath(), protocol.refer(serviceType, url));
            }else {
                invokerMap.put(url.getPath(), invoker);
            }
        }

        urlInvokerMap = invokerMap;
    }


}
