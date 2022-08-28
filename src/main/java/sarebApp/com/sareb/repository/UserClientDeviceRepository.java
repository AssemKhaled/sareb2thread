package sarebApp.com.sareb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sarebApp.com.sareb.entities.UserClientDevice;

import java.util.List;
import java.util.Optional;

/**
 * @author Assem
 */
@Repository
public interface UserClientDeviceRepository extends JpaRepository<UserClientDevice,Long> {

    Optional<UserClientDevice> findByUseridAndDeviceid(Long userId,Long deviceId);
    Optional<List<UserClientDevice>> findAllByUserid(Long userId);
    @Query(value = "select tc_user_client_device.deviceid from tc_user_client_device where tc_user_client_device.userid=:userId", nativeQuery = true)
    public List<Long> getDevicesIds(@Param("userId") Long userId);
}
