import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by juemingzi on 16/5/18.
 */

interface A{

}


public class TestTest {
    private Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

    @Test
    public void test() {
        TestTest testTest = new TestTest();
        testTest.put(String.class, "");

    }

    public <T> void put(Class<T> type, T instance){
        map.put(type, instance);
    }


    public <T> T get(Class<T> type){
        type.cast(map.get(type));
        return (T)map.get(type);
    }

    class AA implements A{

    }

    @Test
    public void testA(){
        AA aa = new AA();

        if(aa instanceof A){
            System.out.println("SS");
        }

    }
}
