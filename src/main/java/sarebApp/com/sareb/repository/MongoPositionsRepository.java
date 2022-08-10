package sarebApp.com.sareb.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import sarebApp.com.sareb.entities.MongoPositions;

import java.util.List;
import java.util.Optional;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoPositionsRepository extends MongoRepository<MongoPositions,String>{

	Optional<List<MongoPositions>> findAllBy_idIn(List<String> ids);
	Integer countByDeviceidIn(List<Long> deviceIds);
	List<MongoPositions> findTop10ByDeviceidAndSpeedOrderByServertimeDesc(Long deviceId , double speed);
	List<MongoPositions> findTop10ByDeviceidAndSpeedAfterOrderByServertimeDesc(Long deviceId , double speed);

	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	List<MongoPositions> deleteByIdIn(List<String> positionIds);
	@Query("{ '_id' : { $in: ?0 } }")
	List<MongoPositions> findByIdIn(List<String> positionIds);
	@Query("{ 'deviceid' : { $in: ?0 } , 'deviceName' : { $exists : false }}")
	List<MongoPositions> findByDeviceIdIn(List<Long> deviceIds,Pageable pageable);

}

