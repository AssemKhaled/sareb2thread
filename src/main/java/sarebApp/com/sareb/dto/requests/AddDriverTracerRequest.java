package sarebApp.com.sareb.dto.requests;

import lombok.*;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AddDriverTracerRequest {
    private Long id;
    private String name;
    private String uniqueId;
    private Object attributes;
}
