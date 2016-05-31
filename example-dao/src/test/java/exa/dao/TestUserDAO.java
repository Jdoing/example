package exa.dao;

import exa.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by juemingzi on 16/3/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TestUserDAO {

    @Autowired
    private UserDAO userDAO;

    @Test
    public void testInsert(){
        User user = new User();
        user.setLastName("Rich");
        user.setFirstName("Jack");
        user.setPhone("13312345678");
        user.setAge(33);
        user.setAddress("zhejiang hangzhou");

        userDAO.insert(user);
    }

    @Test
    public void testFind(){

        System.out.println(userDAO.findByPK(1));

    }

}
