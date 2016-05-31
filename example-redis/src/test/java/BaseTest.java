import org.junit.Before;
import org.junit.Test;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RAtomicLong;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertEquals;

/**
 * Created by juemingzi on 16/3/23.
 */
public class BaseTest {

    private Config config;

    @Before
    public void before() {
        config = new Config();
//        config.setUseLinuxNativeEpoll(true);
//        config.useSingleServer().setAddress("127.0.0.1:6379");
        config.useClusterServers()
                .addNodeAddress("127.0.0.1:7001", "127.0.0.1:7002", "127.0.0.1:7003");
    }

    public class Worker implements Runnable {
        private CyclicBarrier begin;
        private CountDownLatch end;
        private RAtomicLong rAtomicLong;

        public Worker(CyclicBarrier begin, CountDownLatch end, RAtomicLong rAtomicLong) {
            this.begin = begin;
            this.rAtomicLong = rAtomicLong;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                begin.await();
                for (int i = 0; i < loopCount; i++){
                    rAtomicLong.getAndAdd(1);
                }

                System.out.println(Thread.currentThread().getName() + " over");
                end.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    private final int loopCount = 10;
    private final int threadNum = 10;
    @Test
    public void testBase() throws InterruptedException, BrokenBarrierException {
        RedissonClient client = Redisson.create(config);
        RAtomicLong longObject = client.getAtomicLong("myLong");
        longObject.set(0);

        CyclicBarrier begin = new CyclicBarrier(threadNum);
        CountDownLatch end = new CountDownLatch(threadNum);

        for(int i = 0; i < threadNum; i++){

            new Thread(new Worker(begin, end, longObject)).start();
        }

        end.await();

        assertEquals(threadNum * loopCount, longObject.get());

    }

}
