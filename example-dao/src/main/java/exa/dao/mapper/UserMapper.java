package exa.dao.mapper;

import exa.domain.User;

/**
 * Created by juemingzi on 16/3/14.
 */
public interface UserMapper {
    int insert(User user);

    User findByPK(int id);
}
