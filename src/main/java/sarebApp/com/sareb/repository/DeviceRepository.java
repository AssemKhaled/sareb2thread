package sarebApp.com.sareb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sarebApp.com.sareb.entities.Device;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Assem
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device,Long> {

    Optional<List<Device>> findAllByIdIn(List<Long> ids , Pageable pageable);
    Optional<List<Device>> findAllByIdIn(List<Long> ids);
    Optional<List<Device>> findAllByDeleteDateIsNullAndPositionidIsNotNull();
    Optional<List<Device>> findAllByIdInAndDeleteDate(List<Long> ids , String deleteDate , Pageable pageable);
    Optional<List<Device>> findAllByIdInAndDeleteDate(List<Long> ids , String deleteDate );
    Optional<List<Device>> findAllByIdInAndDeleteDateIsNullAndPositionidIsNull(List<Long> ids);
    Optional<List<Device>> findAllByIdInAndDeleteDateIsNullAndPositionidIsNotNull(List<Long> ids);
    Optional<List<Device>> findAllByDeleteDateIsNullAndPositionidIsNull();
    Optional<List<Device>> findAllByUser_idInAndDeleteDateIsNullAndPositionidIsNull( List<Long> userIds);
    Optional<List<Device>> findAllByUser_idInAndDeleteDateIsNullAndPositionidIsNotNull( List<Long> userIds);
    Optional<List<Device>> findAllByIdInAndEndDateGreaterThan(List<Long> ids , Date endDate , Pageable pageable);
    Optional<List<Device>> findAllByIdInAndEndDateGreaterThan(List<Long>ids,Date date);
    Optional<Device> findByIdAndDeleteDate(Long id , String deleteDate);
    Optional<Device> findByUser_idAndDeleteDate(Long userId,String deleteDate);
    Optional<Device> findByIdAndUser_idAndDeleteDateIsNull(Long deviceId,Long userId);
    Optional<List<Device>> findAllByUser_idInAndDeleteDate(List<Long> userId,String deleteDate);
    Optional<List<Device>> findAllByUser_idInAndDeleteDate(List<Long> userId,String deleteDate,Pageable pageable);

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
    @Query(value = "select id FROM tc_devices where user_id IN (:userIds) and tc_devices.delete_date is null",nativeQuery = true)
    List<Integer> clientDeviceIds(@Param("userIds") List<Long> userIds);

    @Query(value = " SELECT  * FROM tc_devices "
            + "  Where ((( uniqueid LIKE LOWER(CONCAT('%',:search, '%')) " +
            "OR name LIKE LOWER(CONCAT('%',:search, '%')) OR " +
            "sequence_number LIKE LOWER(CONCAT('%',:search, '%'))) AND (TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0) ) AND user_id=:userid) limit :offset,:size " ,nativeQuery = true )
    Optional<List<Device>>  getAllDevicesBySearchAndUser_idANDAndEndDate(@Param("search") String search,@Param("offset")int offset,@Param("size")int size,@Param("userid") long userid);
    @Query(value = "select tc_user_client_device.deviceid from tc_user_client_device where tc_user_client_device.userid=:userId", nativeQuery = true)
    List<Integer> newGetDevicesIds(@Param("userId") Long userId);

    @Query(value = "select * FROM tc_devices Where tc_devices.id IN (:deviceIds) AND  uniqueid LIKE LOWER(CONCAT('%',:search, '%'))" +
            "OR name LIKE LOWER(CONCAT('%',:search, '%')) OR " +
            "sequence_number LIKE LOWER(CONCAT('%',:search, '%')) ",nativeQuery = true)
    Page<Device> AdminDeviceListSearch(@Param("deviceIds") List<Long> deviceIds , @Param("search") String search, Pageable pageable);

    @Query(value = "select * FROM tc_devices Where tc_devices.user_id IN (:userIds) AND uniqueid LIKE LOWER(CONCAT('%',:search, '%'))" +
            "OR name LIKE LOWER(CONCAT('%',:search, '%')) OR " +
            "sequence_number LIKE LOWER(CONCAT('%',:search, '%'))  AND delete_date is null ",nativeQuery = true)
    Page<Device> DeviceListSearch(@Param("userIds") List<Long> userIds ,@Param("search") String search,Pageable pageable);

    @Query(value = "select id,name from tc_devices where id IN (:deviceIds ) and delete_date is null",nativeQuery = true)
     List<DeviceSelect> getDeviceSelectByIds(@Param("deviceIds")List<Long> deviceIds);

    @Query(value = "select id,name from tc_devices where user_id=:userId and delete_date is null",nativeQuery = true)
     List<DeviceSelect> getDeviceSelectByIdsclient(@Param("userId")Long userId);

    @Query(value = "SELECT tc_devices.positionid FROM tc_devices "
            + " where tc_devices.lastupdate>date_sub(now(), interval 0 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 3 minute)=false "
            + " AND tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null and tc_devices.positionid is not null AND (TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0) ", nativeQuery = true)
    List<String> getNumberOfOnlineDevicesListByIds(@Param("deviceIds")List<Long> deviceIds);
    @Query(value = "SELECT tc_devices.positionid FROM tc_devices "
            + "where tc_devices.lastupdate>date_sub(now(), interval 3 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 8 minute)=false "
            + " AND tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null and tc_devices.positionid is not null", nativeQuery = true)
     List<String> getNumberOfOutOfNetworkDevicesListByIds(@Param("deviceIds")List<Long> deviceIds);
    @Query(value = "SELECT tc_devices.positionid FROM tc_devices INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id "
            + "where tc_devices.lastupdate>date_sub(now(), interval 0 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 3 minute)=false "
            + " AND tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null and tc_devices.positionid is not null AND (TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)", nativeQuery = true)
     List<String> getNumberOfOnlineDevicesList(@Param("userIds")List<Long> userIds);
    @Query(value = "SELECT tc_devices.positionid FROM tc_devices INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_devices.id "
            + "where tc_devices.lastupdate>date_sub(now(), interval 3 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 8 minute)=false "
            + " AND tc_user_device.userid IN (:userIds) and tc_devices.delete_date is null and tc_devices.positionid is not null AND (TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0) ", nativeQuery = true)
     List<String> getNumberOfOutOfNetworkDevicesList(@Param("userIds")List<Long> userIds);
    @Query(value = "SELECT count(tc_devices.id) FROM tc_devices  " +
            "  WHERE tc_devices.user_id =:userId AND tc_devices.delete_date is null ",nativeQuery = true )
     Integer getTotalNumberOfUserDevicesClient(@Param("userId")Long userId);
    @Query(value = "SELECT count(tc_devices.id) FROM tc_devices INNER JOIN tc_user_device ON tc_devices.id = tc_user_device.deviceid " +
            "AND tc_user_device.userid IN (:userIds) WHERE tc_devices.delete_date is null ",nativeQuery = true )
     Integer getTotalNumberOfUserDevices(@Param("userIds")List<Long> userIds);
    @Query(value = "SELECT * FROM tc_devices where tc_devices.lastupdate>date_sub(now(), interval 0 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 3 minute)=false "
            + " AND tc_devices.user_id =:userId and tc_devices.delete_date is null and tc_devices.positionid is not null AND (TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)", nativeQuery = true)
     List<String> getNumberOfOnlineDevicesListClient(@Param("userId")Long userId);
    @Query(value = "SELECT tc_devices.positionid FROM tc_devices"
            + " where tc_devices.lastupdate>date_sub(now(), interval 3 minute)=false  AND tc_devices.lastupdate<date_sub(now(), interval 8 minute)=false "
            + " AND tc_devices.user_id=:userId and tc_devices.delete_date is null and tc_devices.positionid is not null AND (TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0) ", nativeQuery = true)
     List<String> getNumberOfOutOfNetworkDevicesListClient(@Param("userId")Long userId);
    @Query(value = "SELECT count(tc_devices.id) FROM tc_devices  " +
            " Where tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null ",nativeQuery = true )
     Integer getTotalNumberOfUserDevicesByIds(@Param("deviceIds")List<Long> deviceIds);
    @Query(value = "SELECT id FROM tc_devices  " +
            "  WHERE tc_devices.user_id =:userId AND tc_devices.delete_date is null ",nativeQuery = true )
     List<Long> getDevicesbyclientids(@Param("userId")Long userId);
    @Query(value = "select tc_devices.id FROM tc_devices where tc_devices.id IN (:ids)",nativeQuery = true)
    List<Long>getDevicesByIDs(@Param("ids") List<Long>ids);
    @Query(value = "SELECT tc_devices.positionid FROM tc_devices " +
            " where tc_devices.id IN (:deviceIds) and tc_devices.delete_date is null and tc_devices.positionid is not null "
            + " group by tc_devices.id ",nativeQuery = true )
     List<String> getAllPositionsObjectIdsByIds(@Param("deviceIds")List<Long> deviceIds);

}
