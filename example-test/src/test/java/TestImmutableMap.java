import org.junit.Test;

import java.util.List;

/**
 * Created by juemingzi on 16/3/9.
 */
public class TestImmutableMap {

    static class Person{
        int age;
        String name;

        private Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    @Test
    public void testBase(){
        List<String> strings = null;

//        assertThat(CollectionUtils.size(strings), is(0));

    }

}
