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
public class EventReportResponse {

	private String eventId;
	private String eventType;
	private String serverTime;
	private Object attributes;
	private Long deviceId;
	private String deviceName;
	private Long driverId;
	private String driverName;
	private Long geofenceId;
	private String geofenceName;
	private String positionId;
	private Double latitude;
	private Double longitude;
	private Long userId;


}

