package sarebApp.com.sareb.dto.responses;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DeviceResponse {
    private Long id;
    private String name;
    private String uniqueid;
    private String lastupdate;
    private String positionid;
    private String phone;
    private String model;
    private String plate_num;
    private String right_letter;
    private String middle_letter;
    private String left_letter;
    private Integer plate_type;
    private String brand;
    private String color;
    private String photo;
    private Date startDate;
    private Date endDate;
    private Long driverid;
    private String icon;


}
