import org.junit.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by juemingzi on 16/8/21.
 */
public class PipelineCommanTest {
    private static final SeqGenerator seqGenerater = new SeqGenerator();

    private static class Worker implements Runnable {
        private final CountDownLatch begin;
        private final CountDownLatch end;
        private final Set<String> seqSet;

        public Worker(Set<String> seqSet, CountDownLatch begin, CountDownLatch end) {
            this.seqSet = seqSet;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                begin.await();
                for (int i = 0; i < 1000000; i++) {
                    seqGenerater.setAndExpireWithPipeline();
                }
                System.out.println("end");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                end.countDown();
            }
        }
    }

    @Test
    public void test() throws InterruptedException {
        int cpus = Runtime.getRuntime().availableProcessors();
        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(cpus);
        final Set<String> seqSet = new ConcurrentSkipListSet<>();

        ExecutorService executorService = Executors.newFixedThreadPool(cpus);

        for (int i = 0; i < cpus; i++) {
            executorService.execute(new Worker(seqSet, begin, end));
        }

        long startTime = System.currentTimeMillis();

        begin.countDown();
        end.await();

        System.out.println("elapse: " + (System.currentTimeMillis()-startTime));

        System.out.println("finish!");

    }

}
