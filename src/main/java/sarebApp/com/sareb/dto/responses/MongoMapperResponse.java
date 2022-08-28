package sarebApp.com.sareb.dto.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Assem
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MongoMapperResponse {
//    ArrayList<Map<Object,Object>> lastPoints;

//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    Object attributes;

}
