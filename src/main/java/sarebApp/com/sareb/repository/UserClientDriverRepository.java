package sarebApp.com.sareb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarebApp.com.sareb.entities.UserClientDevice;
import sarebApp.com.sareb.entities.UserClientDriver;

import java.util.List;
import java.util.Optional;

/**
 * @author Assem
 */
public interface UserClientDriverRepository extends JpaRepository<UserClientDriver,Long> {

    Optional<UserClientDriver> findByUseridAndDriverid(Long userId, Long driverId);
    Optional<List<UserClientDriver>> findAllByUserid(Long userId);
}
