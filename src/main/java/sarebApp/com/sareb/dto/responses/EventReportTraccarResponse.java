package sarebApp.com.sareb.dto.responses;

import lombok.*;

/**
 * Bind data of events or notifications from mongo on this model
 * @author fuinco
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventReportTraccarResponse {

	private Long id;
	private Object attributes;
	private Long deviceId;
	private String type;
	private String serverTime;
	private Long positionId;
	private Long geofenceId;
	private Long maintenanceId;
	private String deviceName;
	private String driverName;


}

