package sarebApp.com.sareb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sarebApp.com.sareb.entities.UserClientDevice;

import java.util.List;
import java.util.Optional;

/**
 * @author Assem
 */
@Repository
public interface UserClientDeviceReposiitory extends JpaRepository<UserClientDevice,Long> {

    Optional<UserClientDevice> findByUseridAndDeviceid(Long userId,Long deviceId);
    Optional<List<UserClientDevice>> findAllByUserid(Long userId);
}
