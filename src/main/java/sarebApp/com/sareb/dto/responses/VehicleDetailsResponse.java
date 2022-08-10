package sarebApp.com.sareb.dto.responses;

import lombok.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Assem
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDetailsResponse {

    private int id;
    private String deviceName;
    private String uniqueId;
    private String sequenceNumber;
    private String lastUpdate;
    private String referenceKey;
    private String driverName;
    private String geofenceName;
    private Long driverId;
    private String driverPhoto;
    private String driverUniqueId;
    private String plateType;
    private String plateNum;
    private String rightLetter;
    private String middleLetter;
    private String leftLetter;
    private String ownerName;
    private String ownerId;
    private String userName;
    private String brand;
    private String model;
    private String madeYear;
    private String color;
    private String licenceExptDate;
    private Double carWeight;
    private Double car_weight;
    private String vehiclePlate;
    private String companyName;
    private String positionId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String address;
    private Object attributes;
    private String status;
    private Boolean ignition;
    private Integer sat;
    private Double power;
    private String driver_num;
    private Long companyId;
    private Boolean expired;
    private String simcardNumber;
    private String create_date;
    private String update_date_elm;
    private String delete_date_elm;
    private Long leftDays;
    private String servertime;
    private String devicetime;
    private ArrayList<Map<Object,Object>> lastPoints;

}
