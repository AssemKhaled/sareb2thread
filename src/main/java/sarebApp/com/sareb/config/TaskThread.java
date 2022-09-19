package sarebApp.com.sareb.config;

import org.apache.catalina.LifecycleState;
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.repository.UserRepository;

import java.util.List;

/**
 * @author Assem
 */
public class TaskThread implements Runnable{

    private final UserRepository userRepository;
    List<User> user;
    public TaskThread(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run() {
        user = userRepository.findAll();
    }
}
