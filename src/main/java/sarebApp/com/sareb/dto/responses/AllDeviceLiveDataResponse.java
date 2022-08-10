package sarebApp.com.sareb.dto.responses;

import lombok.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AllDeviceLiveDataResponse {
    private Long id;
    private String deviceName;
    private String uniqueId;
    private String lastUpdate;
    private Double weight;
    private Double latitude;
    private Double operator;
    private Double longitude;
    private String address;
    private ArrayList<Map<Object,Object>> lastPoints;
    private Object attributes;
    private String crash;
    private String batteryUnpluged;
    private String PowerUnpluged;
    private String todayHoursString;
    private String deviceWorkingHoursPerDay;
    private String driverWorkingHoursPerDay;
    private Double power;
    private String photo;
    private Double speed ;
    private String status;
    private String vehicleStatus;
    private String positionId;
    private JSONObject jsonAttributes;
    private String hours;
    private Boolean motion;
    private Boolean valid;
    private String totalDistance;
    private Boolean ignition;
    private String alarm;
    private Double battery;
    private String driverName;
    private String leftLetter;
    private String middleLetter;
    private String rightLetter;
    private String plate_num;
    private String sequence_number;
    private String owner_name;
    private Boolean expired;
    private Long leftDays;
    private Double temperature;
    private Double humidity;
    private String create_date;
}
