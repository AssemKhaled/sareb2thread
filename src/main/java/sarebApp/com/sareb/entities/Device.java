package sarebApp.com.sareb.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	public Date getInquiryTime() {
		return inquiryTime;
	}

	public void setInquiryTime(Date inquiryTime) {
		this.inquiryTime = inquiryTime;
	}

	public String getElmStatus() {
		return elmStatus;
	}

	public void setElmStatus(String elmStatus) {
		this.elmStatus = elmStatus;
	}

	public Boolean getTechnicalRequirements() {
		return technicalRequirements;
	}

	public void setTechnicalRequirements(Boolean technicalRequirements) {
		this.technicalRequirements = technicalRequirements;
	}

	public Boolean getSafetyRequirements() {
		return safetyRequirements;
	}

	public void setSafetyRequirements(Boolean safetyRequirements) {
		this.safetyRequirements = safetyRequirements;
	}

	public Set<User> getUser() {
		return user;
	}

	public void setUser(Set<User> user) {
		this.user = user;
	}

	public Set<Driver> getDriver() {
		return driver;
	}

	public void setDriver(Set<Driver> driver) {
		this.driver = driver;
	}

	public Set<Geofence> getGeofence() {
		return geofence;
	}

	public void setGeofence(Set<Geofence> geofence) {
		this.geofence = geofence;
	}

	
	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "deviceGroup")
    private Set<Group> groups = new HashSet<>();


	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

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


	public Set<Notification> getNotificationDevice() {
		return notificationDevice;
	}

	public void setNotificationDevice(Set<Notification> notificationDevice) {
		this.notificationDevice = notificationDevice;
	}
 
 
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


	public Set<Attribute> getAttributeDevice() {
		return attributeDevice;
	}

	public void setAttributeDevice(Set<Attribute> attributeDevice) {
		this.attributeDevice = attributeDevice;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getUniqueid() {
		return uniqueid;
	}



	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}



	public String getLastupdate() {
		return lastupdate;
	}



	public void setLastupdate(String lastupdate) {
		this.lastupdate = lastupdate;
	}



	public String getPositionid() {
		return positionid;
	}



	public void setPositionid(String positionid) {
		this.positionid = positionid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}



	public String getPlate_num() {
		return plate_num;
	}



	public void setPlate_num(String plate_num) {
		this.plate_num = plate_num;
	}



	public String getRight_letter() {
		return right_letter;
	}



	public void setRight_letter(String right_letter) {
		this.right_letter = right_letter;
	}



	public String getMiddle_letter() {
		return middle_letter;
	}



	public void setMiddle_letter(String middle_letter) {
		this.middle_letter = middle_letter;
	}



	public String getLeft_letter() {
		return left_letter;
	}



	public void setLeft_letter(String left_letter) {
		this.left_letter = left_letter;
	}



	public Integer getPlate_type() {
		return plate_type;
	}



	public void setPlate_type(Integer plate_type) {
		this.plate_type = plate_type;
	}



	public String getReference_key() {
		return reference_key;
	}



	public void setReference_key(String reference_key) {
		this.reference_key = reference_key;
	}



	public Integer getIs_deleted() {
		return is_deleted;
	}



	public void setIs_deleted(Integer is_deleted) {
		this.is_deleted = is_deleted;
	}



	public String getDeleteDate() {
		return deleteDate;
	}



	public void setDeleteDate(String delete_date) {
		this.deleteDate = delete_date;
	}



	public Integer getInit_sensor() {
		return init_sensor;
	}



	public void setInit_sensor(Integer init_sensor) {
		this.init_sensor = init_sensor;
	}



	public Integer getInit_sensor2() {
		return init_sensor2;
	}



	public void setInit_sensor2(Integer init_sensor2) {
		this.init_sensor2 = init_sensor2;
	}



	public Integer getCar_weight() {
		return car_weight;
	}



	public void setCar_weight(Integer car_weight) {
		this.car_weight = car_weight;
	}



	public String getReject_reason() {
		return reject_reason;
	}



	public void setReject_reason(String reject_reason) {
		this.reject_reason = reject_reason;
	}



	public String getSequence_number() {
		return sequence_number;
	}



	public void setSequence_number(String sequence_number) {
		this.sequence_number = sequence_number;
	}



	public Integer getIs_valid() {
		return is_valid;
	}



	public void setIs_valid(Integer is_valid) {
		this.is_valid = is_valid;
	}



	public Integer getExpired() {
		return expired;
	}



	public void setExpired(Integer expired) {
		this.expired = expired;
	}



	public String getCalibrationData() {
		return calibrationData;
	}



	public void setCalibrationData(String calibrationData) {
		this.calibrationData = calibrationData;
	}



	public String getFuel() {
		return fuel;
	}



	public void setFuel(String fuel) {
		this.fuel = fuel;
	}



	public String getSensorSettings() {
		return sensorSettings;
	}



	public void setSensorSettings(String sensorSettings) {
		this.sensorSettings = sensorSettings;
	}



	public String getLineData() {
		return lineData;
	}



	public void setLineData(String lineData) {
		this.lineData = lineData;
	}



	public String getCreate_date() {
		return create_date;
	}



	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}



	public Integer getLastWeight() {
		return lastWeight;
	}



	public void setLastWeight(Integer lastWeight) {
		this.lastWeight = lastWeight;
	}


	public String getOwner_name() {
		return owner_name;
	}



	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getOwner_id() {
		return owner_id;
	}



	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}



	public String getBrand() {
		return brand;
	}



	public void setBrand(String brand) {
		this.brand = brand;
	}



	public String getMade_year() {
		return made_year;
	}



	public void setMade_year(String made_year) {
		this.made_year = made_year;
	}



	public String getColor() {
		return color;
	}



	public void setColor(String color) {
		this.color = color;
	}



	public String getLicense_exp() {
		return license_exp;
	}



	public void setLicense_exp(String license_exp) {
		this.license_exp = license_exp;
	}



	public Integer getDate_type() {
		return date_type;
	}



	public void setDate_type(Integer date_type) {
		this.date_type = date_type;
	}



	public String getPhoto() {
		return photo;
	}



	public void setPhoto(String photo) {
		this.photo = photo;
	}



	public String getIcon() {
		return icon;
	}



	public void setIcon(String icon) {
		this.icon = icon;
	}



	public String getProtocol() {
		return protocol;
	}



	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}



	public String getPort() {
		return port;
	}



	public void setPort(String port) {
		this.port = port;
	}



	public String getDevice_type() {
		return device_type;
	}



	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}



	public Date getRegestration_to_elm_date() {
		return regestration_to_elm_date;
	}



	public void setRegestration_to_elm_date(Date regestration_to_elm_date) {
		this.regestration_to_elm_date = regestration_to_elm_date;
	}



	public String getRepresentative() {
		return representative;
	}



	public void setRepresentative(String representative) {
		this.representative = representative;
	}



	public String getDelete_from_elm() {
		return delete_from_elm;
	}



	public void setDelete_from_elm(String delete_from_elm) {
		this.delete_from_elm = delete_from_elm;
	}



	public Date getDelete_from_elm_date() {
		return delete_from_elm_date;
	}

	public void setDelete_from_elm_date(Date delete_from_elm_date) {
		this.delete_from_elm_date = delete_from_elm_date;
	}



	public Date getUpdate_date_in_elm() {
		return update_date_in_elm;
	}


	public void setUpdate_date_in_elm(Date update_date_in_elm) {
		this.update_date_in_elm = update_date_in_elm;
	}



	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}



	public String getSimcardNumber() {
		return simcardNumber;
	}



	public void setSimcardNumber(String simcardNumber) {
		this.simcardNumber = simcardNumber;
	}


	public Long getUser_id() {
		return user_id;
	}



	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}



	public Double getLastHum() {
		return lastHum;
	}



	public void setLastHum(Double lastHum) {
		this.lastHum = lastHum;
	}



	public Double getLastTemp() {
		return lastTemp;
	}

	
	public void setLastTemp(Double lastTemp) {
		this.lastTemp = lastTemp;
	}



	public Date getStart_date() {
		return start_date;
	}



	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}



	public Date getEnd_date() {
		return endDate;
	}



	public void setEnd_date(Date end_date) {
		this.endDate = end_date;
	}



	public Boolean getActivate_to_elm() {
		return activate_to_elm;
	}



	public void setActivate_to_elm(Boolean activate_to_elm) {
		this.activate_to_elm = activate_to_elm;
	}

	public int getTrackerType() {
		return tracker_type;
	}

	public void setTrackerType(int tracker_type) {
		this.tracker_type = tracker_type;
	}
}

