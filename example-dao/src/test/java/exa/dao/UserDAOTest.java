package exa.dao;

import exa.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByType;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by juemingzi on 16/3/15.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:applicationContext.xml")
@SpringApplicationContext( {"classpath:applicationContext.xml" })
public class UserDAOTest extends UnitilsJUnit4 {

    @SpringBeanByType
    private UserDAO userDAO;

    @Before
    public void before(){
//        System.out.println(System.getProperty("user.home"));
    }

    @Test
    @DataSet
    public void testFindByPK(){

        User user = userDAO.findByPK(4);
        System.out.println(user);
    }

//    @Test
//    public void testBase(){
//        System.out.println(System.getProperty("user.home"));
//    }

    private void params(String f, String... strings){
        assertNotNull(strings);
        System.out.println(strings);
    }

    @Test
    public void testParams(){
        params("1");
    }

}
