package sarebApp.com.sareb.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sarebApp.com.sareb.entities.Device;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Assem
 */
public interface DeviceRepository extends JpaRepository<Device,Long> {

    Optional<List<Device>> findAllByIdIn(List<Long> ids , Pageable pageable);
    Optional<List<Device>> findAllByIdInAndDeleteDate(List<Long> ids , String deleteDate , Pageable pageable);
    Optional<List<Device>> findAllByIdInAndEndDateGreaterThan(List<Long> ids , Date endDate , Pageable pageable);
    Optional<List<Device>> findAllByIdInAndEndDateGreaterThan(List<Long>ids,Date date);

    @Query(value = "select tc_devices.id from tc_devices", nativeQuery = true)
    List<Integer> getAllDeviceIds();
    @Query(value = "select * from tc_devices where tc_devices.id in ( :deviceIds) AND  (uniqueid LIKE LOWER(CONCAT('%',:search, '%')) OR name LIKE LOWER(CONCAT('%',:search, '%')) OR sequence_number LIKE LOWER(CONCAT('%',:search, '%'))) LIMIT :offset,10", nativeQuery = true)
    Optional<List<Device>> getDevicesByDevicesIdsvendor(@Param("deviceIds")List<Long> deviceIds,@Param("search")String search,@Param("offset")int offset);
    @Query(value = "SELECT * FROM tc_devices WHERE (user_id=:userid AND (TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)) limit :offset,:size",nativeQuery = true)
    Optional<List<Device>>  getAllDevicesAndUser_idANDAndEndDate(@Param("offset")int offset,@Param("size")int size,@Param("userid") long userid);
    @Query(value = " SELECT  * FROM tc_devices "
            + "  Where  uniqueid LIKE LOWER(CONCAT('%',:search, '%')) " +
            "OR name LIKE LOWER(CONCAT('%',:search, '%')) OR " +
            "sequence_number LIKE LOWER(CONCAT('%',:search, '%')) limit :offset,:size" ,nativeQuery = true )
    Optional<List<Device>>  getAllDevicesBySearch(@Param("search") String search, @Param("offset")int offset, @Param("size")int size);
    @Query(value = "select tc_devices.id FROM tc_devices INNER JOIN  tc_user_device ON tc_devices.id=tc_user_device.deviceid where tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null",nativeQuery = true)
    List<Integer> deviceIdsByUserChildrens(@Param("userIds") List<Long> userIds);
    @Query(value = " SELECT  * FROM tc_devices "
            + "  Where ((( uniqueid LIKE LOWER(CONCAT('%',:search, '%')) " +
            "OR name LIKE LOWER(CONCAT('%',:search, '%')) OR " +
            "sequence_number LIKE LOWER(CONCAT('%',:search, '%'))) AND (TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0) ) AND user_id=:userid) limit :offset,:size " ,nativeQuery = true )
    Optional<List<Device>>  getAllDevicesBySearchAndUser_idANDAndEndDate(@Param("search") String search,@Param("offset")int offset,@Param("size")int size,@Param("userid") long userid);
    @Query(value = "select tc_user_client_device.deviceid from tc_user_client_device where tc_user_client_device.userid=:userId", nativeQuery = true)
    List<Integer> newGetDevicesIds(@Param("userId") Long userId);


}
