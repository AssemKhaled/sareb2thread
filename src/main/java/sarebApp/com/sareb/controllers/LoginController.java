package sarebApp.com.sareb.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.LoginResponse;
import sarebApp.com.sareb.entities.MongoPositions;
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.exception.ApiRequestException;
import sarebApp.com.sareb.service.Impl.LoginServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * @author Assem
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/app")
public class LoginController {

    private final LoginServiceImpl loginServiceImpl;
    public LoginController(LoginServiceImpl loginServiceImpl) {
        this.loginServiceImpl = loginServiceImpl;
    }


    @GetMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> Login(@RequestHeader(value = "Authorization",defaultValue = "") String authorization){
        try{
            return ResponseEntity.ok(
                    loginServiceImpl.login(authorization)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<User>> test(@RequestParam(value = "Email",defaultValue = "") String email){
        try{
            return ResponseEntity.ok(
                    loginServiceImpl.getUser(email)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping("/getTrips")
    public ResponseEntity<ApiResponse<List<MongoPositions>>> getTrips(@RequestParam(value = "deviceId",defaultValue = "") Long deviceId,
                                                                      @RequestParam(value = "from",defaultValue = "") String from,
                                                                      @RequestParam(value = "to",defaultValue = "") String to){
        try{
            return ResponseEntity.ok(
                    loginServiceImpl.getTrips(deviceId,from,to)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }


}
