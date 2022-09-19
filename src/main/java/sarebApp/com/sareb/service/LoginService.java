package sarebApp.com.sareb.service;

import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.LoginResponse;

/**
 * @author Assem
 */
public interface LoginService {
    ApiResponse<LoginResponse> login(String authorization);
    ApiResponse<?> logout();

}
