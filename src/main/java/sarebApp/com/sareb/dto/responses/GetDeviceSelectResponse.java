package sarebApp.com.sareb.dto.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GetDeviceSelectResponse {
    Long id;
    String name;
}
