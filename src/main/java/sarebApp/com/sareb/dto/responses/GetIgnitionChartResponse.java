package sarebApp.com.sareb.dto.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GetIgnitionChartResponse {
        Integer ignitionON;
        Integer ignitionOFF;
}
