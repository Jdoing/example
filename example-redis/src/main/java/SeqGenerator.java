import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by juemingzi on 16/8/19.
 */
public class SeqGenerator {

    private final JedisPool pool;

    private final byte[] sha1;

    private static final FastDateFormat dateFormat = FastDateFormat.getInstance("yyMMddHHmmssSSS");

    public SeqGenerator() {
        pool = new JedisPool("localhost");
        try {
            sha1 = loadScript();
        } catch (IOException e) {
            throw new RuntimeException("加载文件失败");
        }
    }

    private byte[] loadScript() throws IOException {
        Jedis jedis = getResource();
        try {
            Path path = Paths.get(this.getClass().getResource("lua/test_get_max_id.lua").getPath());
            byte[] luaScript = Files.readAllBytes(path);
            return jedis.scriptLoad(luaScript);
        } finally {
            returnResource(jedis);
        }
    }

    public Jedis getResource() {
        Jedis jedis = pool.getResource();
        return jedis;
    }

    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


    public String getNextSeq() throws IOException {
        Jedis jedis = getResource();
        try {
            String seq = getSeq();
            Object result = jedis.evalsha(sha1, 1, seq.getBytes());
//            Long nextSeq = (Long) result;
//            return nextSeq.toString();
            String ret = new String((byte[])result);
            return ret;
        } finally {
            returnResource(jedis);
        }

    }

    public String getNextSeq(String seq) throws IOException {
        Jedis jedis = getResource();
        try {
            Object result = jedis.evalsha(sha1, 1, seq.getBytes());
//            Long nextSeq = (Long) result;
//            return nextSeq.toString();
            String ret = new String((byte[])result);
            return ret;
        } finally {
            returnResource(jedis);
        }

    }

    private static String getDate() {
        return dateFormat.format(System.currentTimeMillis());
    }

    public String getSeq() {
        return new StringBuilder(17).append(getDate()).append(RandomStringUtils.randomNumeric(2)).toString();
    }

    private static final AtomicLong count = new AtomicLong(0);
    public void setAndExpireWithPipeline(){
        Jedis jedis = getResource();
        try {
            Pipeline pipeline = jedis.pipelined();
            pipeline.set("x", Long.valueOf(count.incrementAndGet()).toString());
            pipeline.expire("x", 600);
            pipeline.sync();

        }finally {
            returnResource(jedis);
        }

    }

    public void setAndExpire(){
        Jedis jedis = getResource();
        try {
            jedis.set("x", Long.valueOf(count.incrementAndGet()).toString());
            jedis.expire("x", 600);
        }finally {
            returnResource(jedis);
        }

    }

    public void setAndExpireWithTran(){
        Jedis jedis = getResource();
        try {
            Transaction transaction = jedis.multi();
            transaction.set("x", Long.valueOf(count.incrementAndGet()).toString());
            transaction.expire("x", 600);
            transaction.exec();
        }finally {
            returnResource(jedis);
        }

    }


    public static void main(String[] args) {


    }
}
