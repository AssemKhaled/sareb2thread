package sarebApp.com.sareb.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.ApiResponseBuilder;
import sarebApp.com.sareb.dto.responses.UserResponse;
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.helper.Impl.DecodePhoto;
import sarebApp.com.sareb.repository.UserRepository;
import sarebApp.com.sareb.service.ProfileService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final LoginServiceImpl loginService;
    @Override
    public ApiResponse<UserResponse> updateProfile(Map<String, String> data, Long userId) {
        ApiResponseBuilder<UserResponse>builder=new ApiResponseBuilder<>();
        User loggedUser=null;
        if (userId!=0){
            Optional<User> user=userRepository.findById(userId);
            if(user.isPresent()){
                loggedUser=user.get();
                if(loggedUser.getDeleteDate()==null){
                    if(data.get("photo")==null){
                        builder.setMessage("Photo is Required");
                        builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(false);
                        return builder.build();
                    }
                    else {
                        DecodePhoto decodePhoto =new DecodePhoto();
                        String photo=data.get("photo").toString();
                        if(loggedUser.getPhoto()!=null){
                            if(!loggedUser.getPhoto().equals("")){
                                if(!loggedUser.getPhoto().equals("not_available.png")){
                                    decodePhoto.deletePhoto(loggedUser.getPhoto(), "user");

                                }
                            }
                        }
                        if(photo==""){
                            loggedUser.setPhoto("not_available.png");
                        }
                        else {
                            if(photo.startsWith("data:image")){
                                loggedUser.setPhoto(decodePhoto.Base64_Image(photo,"user"));
                            }

                        }
                        userRepository.save(loggedUser);
                    }
                }
            }else {
                builder.setMessage("User is Not Found");
                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                builder.setEntity(null);
                builder.setSize(0);
                builder.setSuccess(false);
                return builder.build();
            }

        }else {
            builder.setMessage("UserId is Required");
            builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
            builder.setEntity(null);
            builder.setSize(0);
            builder.setSuccess(false);
            return builder.build();
        }


        builder.setMessage("success");
        builder.setStatusCode(HttpStatus.OK.value());
        builder.setEntity(UserResponse.builder()
                .id(loggedUser.getId()).email(loggedUser.getEmail()).photo(loggedUser.getPhoto())
                .accountType(loggedUser.getAccountType())
                .attribute(loggedUser.getAttribute())
                .clientId(loggedUser.getClientId()).commercialReg(loggedUser.getCommercial_reg()).companyNum(loggedUser.getCompany_num()).companyPhone(loggedUser.getCompany_phone()).IsCompany(loggedUser.getIsCompany()).phone(loggedUser.getPhone())
                .identityNum(loggedUser.getIdentity_num()).createDate(loggedUser.getCreate_date()).dateOfBirth(loggedUser.getDateOfBirth()).dateType(loggedUser.getDateType()).deleteDate(loggedUser.getDeleteDate()).vendorId(loggedUser.getVendorId())
                .isDeleted(loggedUser.getIs_deleted())
                .managerPhone(loggedUser.getManager_phone()).managerName(loggedUser.getManager_name())
                .build());
        builder.setSize(0);
        builder.setSuccess(true);
        return builder.build();
    }

    @Override
    public ApiResponse<UserResponse> getUserInfo(Long userId) {
        ApiResponseBuilder<UserResponse>builder =new ApiResponseBuilder<>();
        User loggedUser=null; Long leftDays = null;Date now = new Date();

        if (userId!=0){
            Optional<User> user=userRepository.findById(userId);
            if(!user.isPresent()){
                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                builder.setStatusCode(404);
                builder.setMessage("User is NOT Found");
                builder.setEntity(null);
                builder.setSize(0);
                return builder.build();
            }
            else {
                loggedUser=user.get();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                Date exp = new Date();
                if (loggedUser.getExp_date() !=null){
                    try {
                        exp = sdf.parse(loggedUser.getExp_date());
                        leftDays = ChronoUnit.DAYS.between(now.toInstant(),exp.toInstant());
                    }catch (ParseException e){
                        leftDays = null;
                        e.printStackTrace();
                    }
                }
                if(loggedUser.getDeleteDate()==null){
                    builder.setMessage("success");
                    builder.setStatusCode(HttpStatus.OK.value());
                    builder.setEntity(UserResponse.builder()
                            .id(loggedUser.getId())
                            .email(loggedUser.getEmail())
                            .photo(loggedUser.getPhoto())
                            .accountType(loggedUser.getAccountType())
                            .attribute(loggedUser.getAttribute())
                            .clientId(loggedUser.getClientId())
                            .commercialReg(loggedUser.getCommercial_reg())
                            .companyNum(loggedUser.getCompany_num())
                            .companyPhone(loggedUser.getCompany_phone())
                            .IsCompany(loggedUser.getIsCompany())
                            .phone(loggedUser.getPhone())
                            .identityNum(loggedUser.getIdentity_num())
                            .createDate(loggedUser.getCreate_date())
                            .dateOfBirth(loggedUser.getDateOfBirth())
                            .dateType(loggedUser.getDateType())
                            .deleteDate(loggedUser.getDeleteDate())
                            .vendorId(loggedUser.getVendorId())
                            .isDeleted(loggedUser.getIs_deleted())
                            .managerPhone(loggedUser.getManager_phone())
                            .managerName(loggedUser.getManager_name())
                            .expDate(loggedUser.getExp_date())
                            .leftDays(leftDays)
                            .build());
                    builder.setSize(0);
                    builder.setSuccess(true);
                    return builder.build();
                }
            }

        }else {
            builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
            builder.setStatusCode(400);
            builder.setMessage("UserId is Required");
            builder.setEntity(null);
            builder.setSize(0);
        }

        builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
        builder.setStatusCode(400);
        builder.setMessage("UserId is Required");
        builder.setEntity(null);
        builder.setSize(0);
        return builder.build();
    }

    @Override
    public ApiResponse<UserResponse> changePassword(Map<String, String> data, Long userId) {
        ApiResponseBuilder<UserResponse>builder =new ApiResponseBuilder<>();
        User loggedUser=null;
        if(userId !=0){
            Optional<User> user=userRepository.findById(userId);
            if (user.isPresent()){
                loggedUser=user.get();
                if(loggedUser.getDeleteDate()==null){
                    if (data.get("oldPassword")==null||data.get("newPassword")==null||data.get("oldPassword")==""||data.get("newPassword")==""){
                        builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        builder.setStatusCode(400);
                        builder.setMessage("newPassword And oldPassword is Required");
                        builder.setEntity(null);
                        builder.setSize(0);
                        return builder.build();
                    }else {
                        String hashedPassword=loginService.getMd5(data.get("oldPassword").toString());
                        String newPassword=loginService.getMd5(data.get("newPassword").toString());
                        String oldPassword= loggedUser.getPassword();
                        if (oldPassword.equals(newPassword)){
                            builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
                            builder.setStatusCode(400);
                            builder.setMessage("OldPassword And NewPassword Is Same");
                            builder.setEntity(null);
                            builder.setSize(0);
                            return builder.build();
                        } else if (hashedPassword.equals(oldPassword)) {
                            loggedUser.setPassword(newPassword);
                            userRepository.save(loggedUser);
                            builder.setMessage("success");
                            builder.setStatusCode(HttpStatus.OK.value());
                            builder.setEntity(UserResponse.builder()
                                    .id(loggedUser.getId()).email(loggedUser.getEmail()).photo(loggedUser.getPhoto())
                                    .accountType(loggedUser.getAccountType())
                                    .attribute(loggedUser.getAttribute())
                                    .clientId(loggedUser.getClientId()).commercialReg(loggedUser.getCommercial_reg()).companyNum(loggedUser.getCompany_num()).companyPhone(loggedUser.getCompany_phone()).IsCompany(loggedUser.getIsCompany()).phone(loggedUser.getPhone())
                                    .identityNum(loggedUser.getIdentity_num()).createDate(loggedUser.getCreate_date()).dateOfBirth(loggedUser.getDateOfBirth()).dateType(loggedUser.getDateType()).deleteDate(loggedUser.getDeleteDate()).vendorId(loggedUser.getVendorId())
                                    .isDeleted(loggedUser.getIs_deleted())
                                    .managerPhone(loggedUser.getManager_phone()).managerName(loggedUser.getManager_name())
                                    .build());
                            builder.setSize(0);
                            builder.setSuccess(true);
                            return builder.build();

                        } else {
                            builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                            builder.setStatusCode(404);
                            builder.setMessage("Wrong OldPassword");
                            builder.setEntity(null);
                            builder.setSize(0);
                            return builder.build();
                        }

                    }
                }

            }else {
                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                builder.setStatusCode(404);
                builder.setMessage("User is NOT Found");
                builder.setEntity(null);
                builder.setSize(0);
                return builder.build();
            }
        }else {
            builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
            builder.setStatusCode(400);
            builder.setMessage("UserId is Required");
            builder.setEntity(null);
            builder.setSize(0);
            return builder.build();
        }
        builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
        builder.setStatusCode(400);
        builder.setMessage("UserId is Required");
        builder.setEntity(null);
        builder.setSize(0);
        return builder.build();
    }

}
