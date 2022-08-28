package sarebApp.com.sareb.dto.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DistanceFuelEngineResponse {
    private String spentFuel;
    private String distance;
    private Long deviceId;
    private String deviceName;
}
