package sarebApp.com.sareb.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.AllDeviceLiveDataResponse;
import sarebApp.com.sareb.exception.ApiRequestException;
import sarebApp.com.sareb.service.Impl.DeviceServiceImpl;

import java.util.List;

/**
 * @author Assem
 */
@RestController
@RequestMapping("/app/dashboard")
public class DeviceController {

    private final DeviceServiceImpl deviceServiceImpl;

    public DeviceController(DeviceServiceImpl deviceServiceImpl) {
        this.deviceServiceImpl = deviceServiceImpl;
    }

    @GetMapping(path = "/getAllDevicesLastInfo")
    public ResponseEntity<ApiResponse<List<AllDeviceLiveDataResponse>>> getAllDevicesLastInfo(@RequestParam(value = "userId", defaultValue = "0") Long userId,
                                                                                              @RequestParam (value = "offset", defaultValue = "0")int offset,
                                                                                              @RequestParam (value = "search", defaultValue = "") String search ){
        try{
            return ResponseEntity.ok(
                    deviceServiceImpl.getAllDeviceDashBoard(userId, offset, search)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }
}
