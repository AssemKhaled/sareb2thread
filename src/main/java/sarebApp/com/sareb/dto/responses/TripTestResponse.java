package sarebApp.com.sareb.dto.responses;

import lombok.*;

import java.util.Date;
import java.util.Map;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TripTestResponse {

    private String id;
    private String protocol;
    private Long deviceId;
    private Date serverTime;
    private Date deviceTime;
    private Date fixTime;
    private Boolean valid;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double speed;
    private Double course;
    private Double accuracy;
    private String address;
    private Map<String, Object> attributes;


    public void set(String key, Double value) {
        if (value != null) {
            attributes.put(key, value);
        }
    }
}
