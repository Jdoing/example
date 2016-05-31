package exa.dao.imp;

import exa.dao.UserDAO;
import exa.dao.mapper.UserMapper;
import exa.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by juemingzi on 16/3/14.
 */
@Component
public class UserDAOImp implements UserDAO {

    @Autowired
    private UserMapper userMapper;


    public int insert(User user) {

        return userMapper.insert(user);
    }

    public User findByPK(int id) {

        return userMapper.findByPK(id);
    }
}
