package sarebApp.com.sareb.dto.responses;

import lombok.*;

/**
 * Bind data from summary report in this model
 * @author Assem
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SummaryReportResponse {

	private String averageSpeed;
	private Long deviceId;
	private String deviceName;
	private String distance;
	private String endOdometer;
	private String engineHours;
	private String maxSpeed;
	private String spentFuel;
	private String startOdometer;
	private String driverName;

}
