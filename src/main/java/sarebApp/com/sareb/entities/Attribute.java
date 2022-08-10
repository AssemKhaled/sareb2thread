package sarebApp.com.sareb.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Model of table tc_attributes in DB
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_attributes" )
public class Attribute extends Attributes{
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "description")
	private String description;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "attribute")
	private String attribute;
	
	@Column(name = "expression")
	private String expression;
	
	@Column(name = "delete_date")
	private String delete_date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getDelete_date() {
		return delete_date;
	}

	public void setDelete_date(String delete_date) {
		this.delete_date = delete_date;
	}
	
	
	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_user_attribute",
            joinColumns = { @JoinColumn(name = "attributeid") },
            inverseJoinColumns = { @JoinColumn(name = "userid") }
    )
	private Set<User> userAttribute = new HashSet<>();

	public Set<User> getUserAttribute() {
		return userAttribute;
	}

	public void setUserAttribute(Set<User> userAttribute) {
		this.userAttribute = userAttribute;
	}

	
	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "attributeGroup")
    private Set<Group> groups = new HashSet<>();
	
	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "attributeDevice")
    private Set<Device> devices = new HashSet<>();

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public Set<Device> getDevices() {
		return devices;
	}

	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}
	
	
}
