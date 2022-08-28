package sarebApp.com.sareb.dto.responses;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetStatusDevices {
    private Integer offlineDevices;
    private Integer onlineDevices;
    private Integer totalDevices;
    private Integer unknownDevices;

}
