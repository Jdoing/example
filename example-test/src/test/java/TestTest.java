import org.junit.Test;

/**
 * Created by juemingzi on 16/6/7.
 */
public class TestTest {
    private static final long YEAR_MILLIS = 365 * 24 * 3600 * 1000L;

    @Test
    public void test(){

        System.out.println(this.getClass().getCanonicalName());
        System.out.println(this.getClass().getSimpleName());
        System.out.println(this.getClass().getName());

        System.out.println(this.getClass().getPackage());
    }

}
