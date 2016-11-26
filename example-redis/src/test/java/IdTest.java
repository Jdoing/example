import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by juemingzi on 16/8/18.
 */
public class IdTest {

    private static Jedis jedis = new Jedis("localhost");

    private static final String ID = "orderId";

//    @BeforeClass
//    public static void setup() {
//        jedis = new Jedis("localhost");
//    }

    private static final FastDateFormat dateFormat = FastDateFormat.getInstance("yyMMddHHmmssSSS");

    @Test
    public void testDate() {
//        String date = dateFormat.format(System.currentTimeMillis());
//        System.out.println(date);

        StringBuilder stringBuilder = new StringBuilder(17).append(getDate()).append(RandomStringUtils.randomNumeric(2));
//
//        System.out.println(stringBuilder.toString());

        System.out.println(stringBuilder);
    }

    private static String getDate() {
        return dateFormat.format(System.currentTimeMillis());
    }

    private static String getSeq() {
        return new StringBuilder(17).append(getDate()).append(RandomStringUtils.randomNumeric(2)).toString();
    }

    @Test
    public void testMakeId() throws IOException {
//        jedis.set(ID, "1");
//        jedis.expire(ID, 1);

        Path path = Paths.get(this.getClass().getResource("lua/test_get_max_id.lua").getPath());
        byte[] luaScript = Files.readAllBytes(path);

        String seq = getSeq();
        System.out.println(seq);

        byte[] sha1 = jedis.scriptLoad(luaScript);

//        Object result = jedis.evalsha(sha1, 1, seq.getBytes());
//        String maxSeq = new String((byte[]) result);
//        System.out.println(maxSeq);

        HashMap<Integer, String> seqMap = new HashMap<>(100000);

        long currTime = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++) {
            Object result = jedis.evalsha(sha1, 1, seq.getBytes());
            String maxSeq = new String((byte[]) result);
            seqMap.put(i, maxSeq);
        }
        System.out.println("elapse: " + (System.currentTimeMillis() - currTime));

    }

    private static final Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource("lua/test_get_max_id.lua").getPath());
    private static byte[] sha1;

    static {
        byte[] luaScript = new byte[0];
        try {
            luaScript = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sha1 = jedis.scriptLoad(luaScript);
    }

    private static String getMaxSeq() throws IOException {
        String seq = getSeq();
        Object result = jedis.evalsha(sha1, 1, seq.getBytes());
        String maxSeq = new String((byte[]) result);

        return maxSeq;
    }

    @Test
    public void testFilePath() throws IOException {
        System.out.println(this.getClass().getResource("lua/test_get_max_id.lua").getPath());
        System.out.println(this.getClass().getResource("lua/test_get_max_id.lua").getFile());
        System.out.println(this.getClass().getResource("lua/test_get_max_id.lua").getContent());

    }

//    private static final Set<String> seqSet = new ConcurrentSkipListSet<>();

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
                for (int i = 0; i < 10000; i++) {
                    String seq = seqGenerater.getNextSeq();
                    if (!seqSet.add(seq)) {
                        System.out.println(seq);
                        fail();
                    }
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
    public void testMulti() throws InterruptedException {
        int cpus = Runtime.getRuntime().availableProcessors();
        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(cpus);
        final Set<String> seqSet = new ConcurrentSkipListSet<>();

        ExecutorService executorService = Executors.newFixedThreadPool(cpus);

        for (int i = 0; i < cpus; i++) {
            executorService.execute(new Worker(seqSet, begin, end));
        }
        begin.countDown();
        end.await();

        assertEquals(seqSet.size(), cpus * 10000);
        System.out.println("finish!");


    }
}
