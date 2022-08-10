package sarebApp.com.sareb.dto.requests;

import lombok.*;

/**
 * @author Assem
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    private String email;
    private String password;

}
