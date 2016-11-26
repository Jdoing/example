import org.junit.Test;

import java.io.IOException;

/**
 * Created by juemingzi on 16/8/19.
 */
public class SeqGeneratorTest {

    @Test
    public void testGetMaxId() throws IOException {
        SeqGenerator generator = new SeqGenerator();
//        String seq = generator.getSeq();
//        System.out.println(seq);

        String ret = generator.getNextSeq("16081915211809289");
        System.out.println(ret);
    }

    @Test
    public void testLong(){
        System.out.println(Long.valueOf("16081915211809408"));

    }

}
