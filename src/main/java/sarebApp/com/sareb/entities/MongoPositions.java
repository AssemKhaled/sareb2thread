package sarebApp.com.sareb.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;


/**
 * Positions Collection in Mongo DB
 * @author fuinco
 *
 */
@Document(collection = "tc_positions")
@Getter
@Setter
public class MongoPositions {
	
	@Id
	private ObjectId _id;
	
	private String protocol;
	
	private Long deviceid;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date servertime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date devicetime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date fixtime;
	
	private Boolean valid;
	
	private Double latitude;
	
	private Double longitude;
	
	private Double altitude;
	
	private Double speed;
	
	private Double course;
	
	private String address;
	
	private Object attributes;
	
	private Double accuracy;
	
	private String network;
	
	private String deviceName;

	private String deviceReferenceKey;

	private String driverReferenceKey;

	private String driverName;

	private Long driverid;

	private Double weight;
	

	public MongoPositions(ObjectId _id, String protocol, Long deviceid, Date servertime, Date devicetime, Date fixtime,
                          Boolean valid, Double latitude, Double longitude, Double altitude, Double speed, Double course,
                          String address, Object attributes, Double accuracy, String network, String deviceName,
                          String deviceReferenceKey, String driverReferenceKey, String driverName, Long driverid, Double weight) {
		super();
		this._id = _id;
		this.protocol = protocol;
		this.deviceid = deviceid;
		this.servertime = servertime;
		this.devicetime = devicetime;
		this.fixtime = fixtime;
		this.valid = valid;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.speed = speed;
		this.course = course;
		this.address = address;
		this.attributes = attributes;
		this.accuracy = accuracy;
		this.network = network;
		this.deviceName = deviceName;
		this.deviceReferenceKey = deviceReferenceKey;
		this.driverReferenceKey = driverReferenceKey;
		this.driverName = driverName;
		this.driverid = driverid;
		this.weight = weight;
	}

	
}
