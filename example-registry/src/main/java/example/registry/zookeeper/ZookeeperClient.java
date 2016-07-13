package example.registry.zookeeper;

import example.common.URL;
import example.registry.ChildListener;
import example.registry.NotifyListener;
import example.registry.StateEvent;
import example.registry.StateListener;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by juemingzi on 16/6/12.
 */
public class ZookeeperClient {

    private CuratorFramework client;

    private Set<StateListener> stateListeners = new HashSet<>();

    private ConcurrentHashMap<URL, Set<NotifyListener>> notifyMap = new ConcurrentHashMap<>();

    private static final String DEFAULT_CONNECT_STRING = "localhost:2181";

    public ZookeeperClient(){
        this(DEFAULT_CONNECT_STRING);
    }

    public ZookeeperClient(String connectString) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                //重连时需要恢复
                if (newState.equals(ConnectionState.RECONNECTED)){
                    try {
                        recovery(StateEvent.RECONNECTED);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("state change: " + newState);
            }
        });

        client.start();
    }

    private void recovery(StateEvent event) throws Exception {
        for (StateListener listener : stateListeners){
            listener.onChanged(event);
        }
    }

    public List<String> getChildNode(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public void create(URL url) throws Exception {
        doCreate(url.getRegisterPath());
    }

    private void doCreate(String path) throws Exception {
        try{
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        }catch (KeeperException.NodeExistsException e){
            System.out.println("exist path: " + path);
        }
    }

    public void addStateListener(StateListener stateListener){
        stateListeners.add(stateListener);
    }

    public List<String> addNodeListener(final URL url, final ChildListener listener) throws Exception {
        //获取订阅地址
        final String path = url.getSubscribePath();
//        String path = "";
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                listener.childChanged(path, client.getChildren().forPath(path));
            }
        });

        return client.getChildren().forPath(path);
    }

    public CuratorFramework getClient() {
        return client;
    }

    public static void main(String[] args) throws Exception {

        final CuratorFramework client;

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                client.getData();
                System.out.println("state change: " + newState);
            }
        });

        client.start();

        String parentPath = "/test";

        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(parentPath);

        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, parentPath, true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("event: " + event);

            }
        });

//        Thread.sleep(1000);

        String childPath = parentPath + '/' + "t1";
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(childPath, "dd".getBytes());

        client.setData().forPath(childPath, "update".getBytes());
        System.out.println("end");


//        System.in.read();

    }

}
