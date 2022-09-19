package sarebApp.com.sareb.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import sarebApp.com.sareb.entities.MongoEvents;
import sarebApp.com.sareb.entities.MongoPositions;

import java.util.Date;
import java.util.List;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoEventsRepository extends MongoRepository<MongoEvents, String>{

	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	public List<MongoEvents> deleteByIdIn(List<String> positionIds);
	List<MongoEvents> findAllByDeviceidAndServertimeBetweenAndType(Long deviceId, Date start, Date end , String type);

	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenAndTypeOrderByServertimeDesc(List<Long> deviceId, Date start,Date end , String type);

	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenAndTypeOrderByServertimeDesc(List<Long> deviceId, Date start, Date end , String type, Pageable pageable);
	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenAndType(List<Long> deviceId, Date start, Date end , String type, Pageable pageable);
	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenAndType(List<Long> deviceId, Date start, Date end , String type);
	List<MongoEvents> findAllByDeviceidInAndServertimeBetween(List<Long> deviceId, Date start, Date end );
	List<MongoEvents> findAllByDeviceidInAndServertimeBetween(List<Long> deviceId, Date start, Date end ,  Pageable pageable );

	Integer countAllByDeviceidInAndServertimeBetweenAndType(List<Long> deviceId, Date start,Date end , String type);

	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenOrderByServertimeDesc(List<Long> deviceId, Date start,Date end);
	@Aggregation(pipeline = {
			"{'$match':{'deviceid':{$in:?0},'servertime':{$gt:?1 , $lte:?2}}}","{'$sort': {'servertime':-1}}"
	})
	List<MongoEvents> findEventsPage(List<Long> deviceId , Date from , Date to,Pageable pageable);
	@Aggregation(pipeline = {
			"{'$match':{'deviceid':?0,'servertime':{$gt:?1 , $lte:?2}}}","{'$sort': {'servertime':-1}}"
	})
	List<MongoEvents> findEvents(List<Long> deviceId , Date from , Date to);

	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenOrderByServertimeDesc(List<Long> deviceId, Date start, Date end ,  Pageable pageable);

	Integer countAllByDeviceidInAndServertimeBetween(List<Long> deviceId, Date start,Date end);
}
