package example.registry.zookeeper;


import example.common.URL;
import example.common.UrlUtils;
import example.registry.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by juemingzi on 16/6/12.
 */
public class ZookeeperRegistry implements Registry {

    private ZookeeperClient zookeeperClient;

    //已经注册过的服务
    private Set<URL> registerServices = new CopyOnWriteArraySet<>();

    //缓存订阅的服务列表
    private final ConcurrentMap<URL, Set<NotifyListener>> subscribeServices = new ConcurrentHashMap<>();

    private final ConcurrentMap<URL, ConcurrentMap<NotifyListener, ChildListener>> zkListeners = new ConcurrentHashMap<>();

    public ZookeeperRegistry() {
        zookeeperClient = new ZookeeperClient();

        zookeeperClient.addStateListener(new StateListener() {
            @Override
            public void onChanged(StateEvent stateEvent) throws Exception {
                if (StateEvent.RECONNECTED.equals(stateEvent)) {
                    recovery();
                }
            }
        });
    }

    //重新注册和订阅
    //TODO 加入重试集合
    private void recovery() throws Exception {
        Set<URL> registers = new HashSet<>(registerServices);
        for (URL url : registers) {
            register(url);
        }

        for(ConcurrentMap.Entry<URL, Set<NotifyListener>> entry : subscribeServices.entrySet()){
            for(NotifyListener listener : entry.getValue()){
                subscribe(entry.getKey(), listener);
            }
        }

        System.out.println("recovery finish");
    }

    @Override
    public void register(URL url) throws Exception {
        zookeeperClient.create(url);
        registerServices.add(url);
    }

    @Override
    public void subscribe(final URL consumerUrl, final NotifyListener notifyListener) throws Exception {
        ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.get(consumerUrl);

        if (listeners == null) {
            zkListeners.putIfAbsent(consumerUrl, new ConcurrentHashMap<NotifyListener, ChildListener>());
            listeners = zkListeners.get(consumerUrl);
        }

        ChildListener childListener = listeners.get(notifyListener);
        if (childListener == null) {
            listeners.putIfAbsent(notifyListener, new ChildListener() {
                @Override
                public void childChanged(String path, List<String> children) throws Exception {
//                    notifyListener.notify(toProviderUrls(consumerUrl, children));
                    ZookeeperRegistry.this.notify(consumerUrl, notifyListener, toProviderUrls(consumerUrl, children));
                }
            });
            childListener = listeners.get(notifyListener);
        }

        List<String> children = zookeeperClient.addNodeListener(consumerUrl, childListener);

        notify(consumerUrl, notifyListener, toProviderUrls(consumerUrl, children));
    }

    @Override
    public List<URL> lookup(URL url) throws Exception {
        List<String> children = zookeeperClient.getChildNode(url.getSubscribePath());

        return toProviderUrls(url, children);
    }

    public void notify(URL consumerURL, NotifyListener listener, List<URL> providerUrls) throws Exception {
        listener.notify(providerUrls);
    }

    /**
     * 将消费方consumer订阅的节点path转换为List<URL>
     *
     * @param consumer
     * @param providers
     * @return
     */
    private List<URL> toProviderUrls(URL consumer, List<String> providers) {
        List<URL> urls = new ArrayList<>();
        if (providers != null && providers.size() > 0) {
            for (String provider : providers) {
                provider = URL.decode(provider);
                URL url = URL.valueOf(provider);
                if (UrlUtils.isMatch(consumer, url)) {//只有匹配的才通知
                    urls.add(url);
                }
            }
        }
        return urls;
    }
}
