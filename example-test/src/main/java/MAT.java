import java.util.HashMap;
import java.util.Map;

/**
 * Created by juemingzi on 16/3/30.
 */

class PO{

}

public class MAT {
    public static final int _1M = 1024 * 1024;

    public static void main(String[] args){

        Map<Integer, byte[]> map = new HashMap<Integer, byte[]>();
        PO po = new PO();
        Integer integer = new Integer(1);
        String string = new String();
        long lg = 1L;

        for (int i = 0; i < 100; i++){
            byte[] bytes = new byte[_1M];
            map.put(i, bytes);

        }

    }
}
