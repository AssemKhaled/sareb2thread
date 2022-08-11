package sarebApp.com.sareb.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.AllDeviceLiveDataResponse;
import sarebApp.com.sareb.dto.responses.VehicleDetailsResponse;
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
                                                                                              @RequestParam(value = "offset", defaultValue = "0")int offset,
                                                                                              @RequestParam(value = "search", defaultValue = "") String search ){
        try{
            return ResponseEntity.ok(
                    deviceServiceImpl.getAllDevicesDashBoard(userId, offset, search)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }
    @GetMapping(path = "/getDeviceDetails")
    public ResponseEntity<ApiResponse<VehicleDetailsResponse>> getDeviceDetails(@RequestParam(value = "userId", defaultValue = "0") Long userId,
                                                                                @RequestParam(value = "deviceId", defaultValue = "0")Long deviceId){
        try{
            return ResponseEntity.ok(
                    deviceServiceImpl.getDeviceDetails(userId, deviceId)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "/getDevicesList")
    public ResponseEntity<ApiResponse<List<VehicleDetailsResponse>>> getDevicesList(@RequestParam(value = "userId", defaultValue = "0") Long userId,
                                                                                    @RequestParam(value = "offset", defaultValue = "0")int offset,
                                                                                    @RequestParam(value = "search", defaultValue = "")String search){
        try{
            return ResponseEntity.ok(
                    deviceServiceImpl.getDevicesList(userId, offset,search)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }



}
