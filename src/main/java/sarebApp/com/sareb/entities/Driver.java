package sarebApp.com.sareb.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * 
 * Model of table tc_drivers in DB
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_drivers")
@JsonIgnoreProperties(value = { "device" })
@Data
public class Driver extends Attributes{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "uniqueid")
	private String uniqueid;
	
	@Column(name = "mobile_num")
	private String mobile_num;
	
	@Column(name = "birth_date")
	private String birth_date;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "reference_key")
	private String reference_key;
	
	@Column(name = "is_deleted")
	private Integer is_deleted=null;
	
	@Column(name = "delete_date")
	private String deleteDate =null;
	
	@Column(name = "reject_reason")
	private String reject_reason;
	
	@Column(name = "date_type")
	private Integer date_type=null;
	
	@Column(name = "is_valid")
	private Integer is_valid=null;
	
	@Column(name = "photo")
	private String photo;
	
	@Column(name = "regestration_to_elm_date")
	private Date regestration_to_elm_date;
	
	@Column(name = "delete_from_elm_date")
	private Date delete_from_elm_date;
	
	@Column(name = "update_date_in_elm")
	private Date update_date_in_elm;

	@Column(name = "user_id")
	private Long userId;
	
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "driver"
    )
    private Set<Device> device = new HashSet<>();

	@JsonIgnore 
	@ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "tc_user_driver",joinColumns = { @JoinColumn(name = "driverid") },
    inverseJoinColumns = { @JoinColumn(name = "userid") })
    private Set<User> userDriver = new HashSet<>();
	
	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "driverGroup")
    private Set<Group> groups = new HashSet<>();


}
