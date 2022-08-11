package sarebApp.com.sareb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;
import sarebApp.com.sareb.entities.Driver;

import java.util.List;
import java.util.Optional;

/**
 * @author Assem
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {

    Optional<Driver> findByIdAndDeleteDate(Long id ,String deleteDate);
    Optional<List<Driver>> findAllByIdInAndDeleteDate(List<Long> ids,String deleteDate);
    Optional<List<Driver>> findAllByIdIn(List<Long> ids);

    @Query(value = "SELECT driverid FROM tc_device_driver WHERE deviceid =:deviceId",nativeQuery = true)
    Long deviceDriverId(@Param("deviceId") Long deviceId);
    @Query(value = "SELECT driverid FROM tc_device_driver WHERE deviceid IN (:deviceId)",nativeQuery = true)
    List<Long> deviceDriverIds(@Param("deviceId") List<Long> deviceId);
}
