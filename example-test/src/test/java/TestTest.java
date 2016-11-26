import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by juemingzi on 16/6/7.
 */
public class TestTest {
    private static final long YEAR_MILLIS = 365 * 24 * 3600 * 1000L;

    @Test
    public void test() throws Exception {
        InetAddress ia = InetAddress.getLocalHost();//获取本地IP对象
        System.out.println("MAC ......... " + getMACAddress(ia));
    }

    private static String getMACAddress(InetAddress ia) throws Exception {
        //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

        //下面代码是把mac地址拼装成String
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            //mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            sb.append(s.length() == 1 ? 0 + s : s);
        }

        //把字符串所有小写字母改为大写成为正规的mac地址并返回
        return sb.toString().toUpperCase();
    }

    @Test
    public void testConfig() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        InetAddress.getLocalHost().getAddress();
        String ip=addr.getHostAddress().toString(); //获取本机ip
        String hostName=addr.getHostName().toString(); //获取本机计算机名称
        System.out.println("本机IP："+ip+"\n本机名称:"+hostName);
        Properties props=System.getProperties();
        System.out.println("操作系统的名称："+props.getProperty("os.name"));
        System.out.println("操作系统的版本："+props.getProperty("os.version"));
    }

    @Test
    public void getMac(){
        List<String> strings = new ArrayList<>();

        Multiset<Integer> lengths = HashMultiset.create(
                FluentIterable.from(strings)
                        .filter(new Predicate<String>() {
                            public boolean apply(String string) {
                                return CharMatcher.JAVA_UPPER_CASE.matchesAllOf(string);
                            }
                        })
                        .transform(new Function<String, Integer>() {
                            public Integer apply(String string) {
                                return string.length();
                            }
                        }));


    }

    @Test
    public void testArray(){
        Set<String> keys = Sets.newHashSet();

    }


}
