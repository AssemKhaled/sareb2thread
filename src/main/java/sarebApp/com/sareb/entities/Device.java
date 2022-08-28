package sarebApp.com.sareb.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Model of table tc_devices in DB
 * @author fuinco
 *
 */

@Entity
@Table(name = "tc_devices")
@Setter
@Getter
public class Device extends Attributes{

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name") 
	private String name;
	
	@Column(name = "uniqueid")
	private String uniqueid;
	
	@Column(name = "lastupdate")
	private String lastupdate;
	
	@Column(name = "positionid")
	private String positionid;
	
	@Column(name = "position_id")
	private String position_id;
	
	@Column(name = "phone") 
	private String phone;
	
	@Column(name = "model")
	private String model;
	
	@Column(name = "plate_num")
	private String plate_num;
	
	@Column(name = "right_letter")
	private String right_letter;
	
	@Column(name = "middle_letter")
	private String middle_letter;
	
	@Column(name = "left_letter")
	private String left_letter;
	
	@Column(name = "plate_type")
	private Integer plate_type;
	
	@Column(name = "reference_key")
	private String reference_key;
	
	@Column(name = "is_deleted")
	private Integer is_deleted=null;
	
	@Column(name = "delete_date")
	private String deleteDate =null;
	
    @Column(name = "init_sensor")
	private Integer init_sensor;
	
	@Column(name = "init_sensor2")
	private Integer init_sensor2;
	
	@Column(name = "car_weight")
	private Integer car_weight;
	
	@Column(name = "reject_reason")
	private String reject_reason;
	
	@Column(name = "sequence_number")
	private String sequence_number;
	
	@Column(name = "is_valid")
	private Integer is_valid;
	
	@Column(name = "expired")
	private Integer expired;
	
	@Column(name = "calibrationData",length=1080)
	private String calibrationData;
	
	@Column(name = "fuel",length=1080)
	private String fuel;
	
	@Column(name = "sensorSettings",length=1080)
	private String sensorSettings;
	
	@Column(name = "lineData")
	private String lineData;
	
	@Column(name = "create_date")
	private String create_date;
	
	@Column(name = "lastWeight")
	private Integer lastWeight;
	
	@Column(name = "owner_name")
	private String owner_name;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "owner_id")
	private String owner_id;
	
	@Column(name = "brand")
	private String brand;
	
	@Column(name = "made_year")
	private String made_year;
	
	@Column(name = "color")
	private String color;
	
	@Column(name = "license_exp")
	private String license_exp;
	
	@Column(name = "date_type")
	private Integer date_type;

	@Column(name = "photo")
	private String photo;
	
	@Column(name = "icon")
	private String icon;
	
	@Column(name = "protocol")
	private String protocol;
	
	@Column(name = "port")
	private String port;
	
	@Column(name = "device_type")
	private String device_type;
	
	@Column(name = "regestration_to_elm_date")
	private Date regestration_to_elm_date;
	
	@Column(name = "representative")
	private String representative;
	
	@Column(name = "delete_from_elm")
	private String delete_from_elm;
	
	@Column(name = "delete_from_elm_date")
	private Date delete_from_elm_date;
	
	@Column(name = "update_date_in_elm")
	private Date update_date_in_elm;
	
	@Column(name = "simcardNumber")
	private String simcardNumber;
	
	@Column(name = "user_id")
	private Long user_id;
	
	@Column(name = "lastHum")
	private Double lastHum = 0.0;
	
	@Column(name = "lastTemp")
	private Double lastTemp = 0.0;
	
	@Column(name = "start_date")
	private Date start_date;
	
	@Column(name = "end_date")
	private Date endDate;
	
	@Transient
	private Boolean activate_to_elm;

	@Column(name = "inquiry_time")
	private Date inquiryTime;

	@Column(name = "elm_status")
	private String elmStatus;

	@Column(name = "technical_requirements")
	private Boolean technicalRequirements;

	@Column(name = "tracker_type")
	private int tracker_type;

	@Column(name = "safety_requirements")
	private Boolean safetyRequirements;

	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_user_device",
            joinColumns = { @JoinColumn(name = "deviceid") },
            inverseJoinColumns = { @JoinColumn(name = "userid") }
    )
	
	private Set<User> user = new HashSet<>();

	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_device_driver",
            joinColumns = { @JoinColumn(name = "deviceid") },
            inverseJoinColumns = { @JoinColumn(name = "driverid") }
    )

	private Set<Driver> driver = new HashSet<>();

	@JsonIgnore
	@ManyToMany(
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE}
			)
	@JoinTable(
			name = "tc_device_geofence",
			joinColumns = {@JoinColumn (name = "deviceid")},
			inverseJoinColumns = {@JoinColumn(name = "geofenceid")}
			)
	private Set<Geofence> geofence = new HashSet<>();
   

	public Device() {
		
	}

	public Device(Long id, String name, String uniqueid, String lastupdate, String positionid, String position_id, String phone, String model, String plate_num, String right_letter, String middle_letter, String left_letter, Integer plate_type, String reference_key, Integer is_deleted, String deleteDate, Integer init_sensor, Integer init_sensor2, Integer car_weight, String reject_reason, String sequence_number, Integer is_valid, Integer expired, String calibrationData, String fuel, String sensorSettings, String lineData, String create_date, Integer lastWeight, String owner_name, String username, String owner_id, String brand, String made_year, String color, String license_exp, Integer date_type, String photo, String icon, String protocol, String port, String device_type, Date regestration_to_elm_date, String representative, String delete_from_elm, Date delete_from_elm_date, Date update_date_in_elm, String simcardNumber, Long user_id, Double lastHum, Double lastTemp, Date start_date, Date end_date, Boolean activate_to_elm, Date inquiryTime, String elmStatus, Boolean technicalRequirements, Boolean safetyRequirements, Set<User> user, Set<Driver> driver, Set<Geofence> geofence, Set<Group> groups, Set<Notification> notificationDevice, Set<Attribute> attributeDevice,int tracker_type) {
		this.id = id;
		this.name = name;
		this.tracker_type=tracker_type;
		this.uniqueid = uniqueid;
		this.lastupdate = lastupdate;
		this.positionid = positionid;
		this.position_id = position_id;
		this.phone = phone;
		this.model = model;
		this.plate_num = plate_num;
		this.right_letter = right_letter;
		this.middle_letter = middle_letter;
		this.left_letter = left_letter;
		this.plate_type = plate_type;
		this.reference_key = reference_key;
		this.is_deleted = is_deleted;
		this.deleteDate = deleteDate;
		this.init_sensor = init_sensor;
		this.init_sensor2 = init_sensor2;
		this.car_weight = car_weight;
		this.reject_reason = reject_reason;
		this.sequence_number = sequence_number;
		this.is_valid = is_valid;
		this.expired = expired;
		this.calibrationData = calibrationData;
		this.fuel = fuel;
		this.sensorSettings = sensorSettings;
		this.lineData = lineData;
		this.create_date = create_date;
		this.lastWeight = lastWeight;
		this.owner_name = owner_name;
		this.username = username;
		this.owner_id = owner_id;
		this.brand = brand;
		this.made_year = made_year;
		this.color = color;
		this.license_exp = license_exp;
		this.date_type = date_type;
		this.photo = photo;
		this.icon = icon;
		this.protocol = protocol;
		this.port = port;
		this.device_type = device_type;
		this.regestration_to_elm_date = regestration_to_elm_date;
		this.representative = representative;
		this.delete_from_elm = delete_from_elm;
		this.delete_from_elm_date = delete_from_elm_date;
		this.update_date_in_elm = update_date_in_elm;
		this.simcardNumber = simcardNumber;
		this.user_id = user_id;
		this.lastHum = lastHum;
		this.lastTemp = lastTemp;
		this.start_date = start_date;
		this.endDate = end_date;
		this.activate_to_elm = activate_to_elm;
		this.inquiryTime = inquiryTime;
		this.elmStatus = elmStatus;
		this.technicalRequirements = technicalRequirements;
		this.safetyRequirements = safetyRequirements;
		this.user = user;
		this.driver = driver;
		this.geofence = geofence;
		this.groups = groups;
		this.notificationDevice = notificationDevice;
		this.attributeDevice = attributeDevice;
	}


	
	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "deviceGroup")
    private Set<Group> groups = new HashSet<>();


	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_device_notification",
            joinColumns = { @JoinColumn(name = "deviceid") },
            inverseJoinColumns = { @JoinColumn(name = "notificationid") }
    )
	private Set<Notification> notificationDevice= new HashSet<>();



	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_device_attribute",
            joinColumns = { @JoinColumn(name = "deviceid") },
            inverseJoinColumns = { @JoinColumn(name = "attributeid") }
    )
	private Set<Attribute> attributeDevice= new HashSet<>();

	@Column(name = "driverid")
	private Long driverId;
}

