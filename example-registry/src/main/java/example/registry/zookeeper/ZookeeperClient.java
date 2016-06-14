package example.registry.zookeeper;

import example.registry.StateListener;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;

/**
 * Created by juemingzi on 16/6/12.
 */
public class ZookeeperClient {

    private CuratorFramework client;

    public ZookeeperClient() {
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
    }

    public void create(String path) throws Exception {
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
    }

    public void delete(String path) throws Exception {

        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

    public void addStateListener(String path, StateListener stateListener) throws Exception {
        final NodeCache nodeCache = new NodeCache(client, path);
        nodeCache.start();

        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(new String(data));
            }
        });


    }


    public void close() {
        CloseableUtils.closeQuietly(client);
    }
}
