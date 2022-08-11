package sarebApp.com.sareb.entities;

import lombok.Data;

import javax.persistence.*;

/**
 *  
 * Model of tc_user_client_device used in type 4 to save his devices
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_user_client_device")
@Data
public class UserClientDevice {

	@Id @GeneratedValue @Column(name = "id")
	private Long id;

	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "deviceid")
	private Long deviceid;



}
