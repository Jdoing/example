import org.junit.Test;

/**
 * Created by juemingzi on 16/6/7.
 */
public class TestTest {
    private static final long YEAR_MILLIS = 365 * 24 * 3600 * 1000L;

    @Test
    public void test() {

        int v = 1;

        for (int i = 8; i <= 56; i += 8) {
            System.out.println("位移" + i + "位：" + (v >>> i));
        }

        //        System.out.println(Integer.toHexString(v));
//
//        System.out.println(Integer.toHexString(0x01 >> 32));

        System.out.println("====");

        byte b = 6;
        System.out.println(b >>> 32);

    }

}
