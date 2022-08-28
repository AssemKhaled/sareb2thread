package sarebApp.com.sareb.service;

import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.UserResponse;

import java.util.Map;

public interface ProfileService {

    ApiResponse<UserResponse> updateProfile(Map<String,String> data,Long userId);
    ApiResponse<UserResponse> getUserInfo(Long userId);
    ApiResponse<UserResponse>changePassword(Map<String,String>data,Long userId);

}
