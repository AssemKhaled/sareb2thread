package sarebApp.com.sareb.entities;

import lombok.Data;

import javax.persistence.*;

/**
 * Model of tc_user_client_driver used in type 4 to save his drivers
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_user_client_driver")
@Data
public class UserClientDriver {

	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "driverid")
	private Long driverid;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	
}
