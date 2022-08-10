package sarebApp.com.sareb.service;

import sarebApp.com.sareb.entities.User;

import java.util.List;

/**
 * @author Assem
 */
public interface UserService {

    User findById(Long userId);
    void resetChildernArray();
    List<User> getActiveAndInactiveChildern(Long userId);

}
