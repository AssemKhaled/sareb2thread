package sarebApp.com.sareb.dto.responses;


import lombok.*;

/**
 * Bind data of stop report in this model 
 * @author Assem
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class StopReportResponse {
	
	private String address;
	private String averageSpeed;
	private Long deviceId;
	private String deviceName;
	private String distance;
	private String duration;
	private String endOdometer;
	private String endTime;
	private String engineHours;
	private String latitude;
	private String longitude;
	private String maxSpeed;
	private String positionId;
	private String spentFuel;
	private String startOdometer;
	private String startTime;
	private String driverName;
	private String driverUniqueId;

}
