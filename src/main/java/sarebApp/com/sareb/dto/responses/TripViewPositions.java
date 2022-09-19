package sarebApp.com.sareb.dto.responses;

import lombok.*;

/**
 * @author Assem
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TripViewPositions {

    private Double lat;
    private Double lon;
    private Double course;


}
