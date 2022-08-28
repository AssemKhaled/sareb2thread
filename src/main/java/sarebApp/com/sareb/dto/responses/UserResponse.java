package sarebApp.com.sareb.dto.responses;

import lombok.*;
import sarebApp.com.sareb.entities.Attribute;

import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String identityNum;
    private String companyNum;
    private String managerName;
    private String managerPhone;
    private String commercialReg;
    private String dateOfBirth;
    private Integer dateType;
    private String referenceKey;
    private Integer isDeleted;
    private String companyPhone;
    private String deleteDate;
    private String photo;
    private String rejectReason;
    private Integer IsCompany;
    private Long vendorId;
    private Long clientId;
    private Integer accountType;
    private String createDate;
    private Set<Attribute> attribute = new HashSet<>();

}
