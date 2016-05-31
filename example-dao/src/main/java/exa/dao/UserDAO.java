package exa.dao;

import exa.domain.User;

/**
 * Created by juemingzi on 16/3/14.
 */

public interface UserDAO {

    int insert(User user);

    User findByPK(int id);
}
