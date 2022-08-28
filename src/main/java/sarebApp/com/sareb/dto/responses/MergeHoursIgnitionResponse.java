package sarebApp.com.sareb.dto.responses;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MergeHoursIgnitionResponse {
    List<Map> hours;
    Map status;
}
