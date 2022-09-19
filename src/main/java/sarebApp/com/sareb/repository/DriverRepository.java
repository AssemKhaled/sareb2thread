package sarebApp.com.sareb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;
import sarebApp.com.sareb.dto.responses.DriverSelectResponse;
import sarebApp.com.sareb.entities.Device;
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
    Optional<List<Driver>> findAllByUserIdInAndDeleteDate(List<Long> userIds,String deleteDate);
    Optional<List<Driver>> findAllByUserIdInAndDeleteDate(List<Long> userIds,String deleteDate,Pageable pageable);
    Optional<Driver> findByUserIdAndDeleteDate(Long userId,String deleteDate);
    Optional<List<Driver>> findAllByIdIn(List<Long> ids);
    Optional<List<Driver>> findAllByDeleteDateIsNull();
    Optional<List<Driver>> findAllByIdIn(List<Long> ids,Pageable pageable);

    @Query(value = "SELECT driverid FROM tc_device_driver WHERE deviceid =:deviceId",nativeQuery = true)
    Long deviceDriverId(@Param("deviceId") Long deviceId);
    @Query(value = "SELECT driverid FROM tc_device_driver WHERE deviceid IN (:deviceId)",nativeQuery = true)
    List<Long> deviceDriverIds(@Param("deviceId") List<Long> deviceId);

    @Query(value = "SELECT id FROM tc_drivers",nativeQuery = true)
    List<Integer> getAllDriverIds();

    @Query(value = "select id FROM tc_drivers where user_id IN (:userIds) and tc_drivers.delete_date is null",nativeQuery = true)
    List<Integer> clientDriverIds(@Param("userIds") List<Long> userIds);

    @Query(value = "select * FROM tc_drivers Where tc_drivers.id IN (:driverIds) AND  uniqueid LIKE LOWER(CONCAT('%',:search, '%'))" +
            "OR name LIKE LOWER(CONCAT('%',:search, '%')) OR " +
            "mobile_num LIKE LOWER(CONCAT('%',:search, '%'))",nativeQuery = true)
    Page<Driver> AdminDriverListSearch(@Param("driverIds") List<Long> driverIds , @Param("search") String search,Pageable pageable);

    @Query(value = "select * FROM tc_drivers Where tc_drivers.user_id IN (:userIds) AND uniqueid LIKE LOWER(CONCAT('%',:search, '%'))" +
            "OR name LIKE LOWER(CONCAT('%',:search, '%')) OR " +
            "mobile_num LIKE LOWER(CONCAT('%',:search, '%'))  AND delete_date is null ",nativeQuery = true)
    Page<Driver> DriverListSearch(@Param("userIds") List<Long> userIds ,@Param("search") String search,Pageable pageable);

}
