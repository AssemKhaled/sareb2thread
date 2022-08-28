package sarebApp.com.sareb.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
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
@Data
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

	
}
