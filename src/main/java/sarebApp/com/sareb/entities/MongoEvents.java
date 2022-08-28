package sarebApp.com.sareb.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

/**
 * Events collection in Mongo DB
 * @author fuinco
 *
 */
@Document(collection = "tc_events")
@Data
public class MongoEvents {

	@Id
	private ObjectId _id;
	
	private String type;
	
	private Date servertime;
	
	private Long deviceid;

	private String positionid;
	
	private Long geofenceid;
	
	private Object attributes;
	
	private Long maintinanceid;
	
	private String deviceName;
	
	private Long driverid;
	
	private String driverName;
	
	private Long userId;

}
