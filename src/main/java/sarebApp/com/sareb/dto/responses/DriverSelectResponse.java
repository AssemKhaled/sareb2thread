package sarebApp.com.sareb.dto.responses;

import lombok.*;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DriverSelectResponse {
    private Long id;
    private String name;
}
