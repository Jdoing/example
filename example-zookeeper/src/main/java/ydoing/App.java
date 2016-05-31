package ydoing;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 */
public class App {

    //创建重连策略
    static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    static String path = "/example";

    //创建会话
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(retryPolicy)
            .build();

    public void basicOperation() throws Exception {
        client.start();

        //创建节点
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());

        //读取数据节点
        byte[] data = client.getData().forPath(path);
        System.out.println("init data is: " + new String(data));

        //更新数据节点
        client.setData().forPath(path, "update data".getBytes());

        //删除数据节点
        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

    public void asyncCreateDataNode() throws Exception {
        client.start();

        final CountDownLatch cdl = new CountDownLatch(1);

        /* 创建节点 */
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                //创建成功后回调方法
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        System.out.println("node name is: " + curatorEvent.getName());
                        System.out.println("node path is: " + curatorEvent.getPath());
                        System.out.println("event type: " + curatorEvent.getType());
                        cdl.countDown();
                    }
                })
                .forPath(path, "init".getBytes());
        System.out.println("already commit!");

        cdl.await();
    }


    public static void watchNode() throws Exception {
        client.start();
        //创建节点

        final NodeCache nodeCache = new NodeCache(client, path);
        nodeCache.start();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(new String(data));
                countDownLatch.countDown();
            }
        });

        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path + "/child", "child data".getBytes());

        countDownLatch.await();
    }

    public static void watchChildrenNode() throws Exception {
        client.start();
        //创建节点

        final NodeCache nodeCache = new NodeCache(client, path);
        nodeCache.start();

        final CountDownLatch countDownLatch = new CountDownLatch(2);
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("event type: " + pathChildrenCacheEvent.getType());
                System.out.println("node data: " + pathChildrenCacheEvent.getData());
                countDownLatch.countDown();
            }
        });
        client.create().withMode(CreateMode.EPHEMERAL).forPath(path + "/child", "child data".getBytes());
        System.out.println("init child data: " + new String(client.getData().forPath(path + "/child")));

        client.setData().forPath(path + "/child", "set child data".getBytes());
        countDownLatch.await();
    }

    public static void main(String[] args) throws Exception {
        watchChildrenNode();
    }
}
