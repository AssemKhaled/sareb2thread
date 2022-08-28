package sarebApp.com.sareb.dto.responses;

import lombok.*;

/**
 * Bind data of trip report in this model
 * @author Assem
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TripReportResponse {

	private String averageSpeed;
	private Long deviceId;
	private String deviceName;
	private String distance;
	private String driverName;
	private String driverUniqueId;
	private String duration;
	private String endAddress;
	private String endLat;
	private String endLon;
	private String endOdometer;
	private String endPositionId;
	private String endTime;
	private String maxSpeed;
	private String spentFuel;
	private String startAddress;
	private String startLat;
	private String startLon;
	private String startOdometer;
	private String startPositionId;
	private String startTime;
	private String companyName;
	private Double temp;
	private Double hum;

}