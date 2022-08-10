package sarebApp.com.sareb.dto.responses;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private String jwt;
    private String email;

}
