package sarebApp.com.sareb.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.UserResponse;
import sarebApp.com.sareb.exception.ApiRequestException;
import sarebApp.com.sareb.service.Impl.ProfileServiceImpl;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(path = "/app/profile")
public class ProfileController {
    private final ProfileServiceImpl profileServiceimp;

    public ProfileController(ProfileServiceImpl profileServiceimp) {
        this.profileServiceimp = profileServiceimp;
    }


    @PostMapping (path="/updatePhoto")
    public ResponseEntity<ApiResponse<UserResponse>> updatePhoto(@RequestParam(value = "userId", defaultValue = "0") Long userId,@RequestBody Map<String, String> data){
        try {
            return ResponseEntity.ok(profileServiceimp.updateProfile(data,userId));
        }catch (Exception |Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
}
    @GetMapping (path="/getProfileInfo")
    public ResponseEntity<ApiResponse<UserResponse>> getProfileInfo(@RequestParam(value = "userId", defaultValue = "0") Long userId){
        try {
            return ResponseEntity.ok(profileServiceimp.getUserInfo(userId));
        }catch (Exception |Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
        //ATBBBWL9jmsVXREdG94uQcVUKwZTC7385EE8

    }
    @PostMapping(path = "/changePasword")
    public ResponseEntity<ApiResponse<UserResponse>>changePassword(@RequestBody Map<String, String> data , @RequestParam (value = "userId", defaultValue = "0") Long userId){
        try {
            return ResponseEntity.ok(profileServiceimp.changePassword(data,userId));
        }catch (Exception|Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }
    }
