package sarebApp.com.sareb.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.vladmihalcea.hibernate.type.json.JsonType;
import com.vladmihalcea.hibernate.type.json.internal.JsonBytesSqlTypeDescriptor;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.json.JSONObject;
import springfox.documentation.spring.web.json.Json;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Assem
 */
@Builder
@Entity
@Table(name = "thread_processed_data")
@TypeDefs({@TypeDef(name = "json", typeClass = JsonType.class)})
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ThreadProcessedData  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "statusCode")
    private Integer statusCode;
    @Column(name = "success")
    private Boolean success;
    @Column(name = "message")
    private String message;
    @Column(name = "size")
    private Integer size;
    @Type(type = "json")
    @Column(name = "entity",columnDefinition = "json")
    private Map<Long,Object> entity;
    @Column(name = "threadStatus")
    private String threadStatus;
}
