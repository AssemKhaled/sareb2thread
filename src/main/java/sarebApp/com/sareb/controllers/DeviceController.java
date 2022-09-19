package sarebApp.com.sareb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.*;
import sarebApp.com.sareb.entities.Device;
import sarebApp.com.sareb.entities.Driver;
import sarebApp.com.sareb.exception.ApiRequestException;
import sarebApp.com.sareb.service.Impl.DeviceServiceImpl;

import java.util.List;

/**
 * @author Assem
 */
@CrossOrigin
@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceServiceImpl deviceServiceImpl;


    @GetMapping(path = "/getDeviceLiveDataMap")
    public ResponseEntity<ApiResponse<List<DevicesMapResponse>>> getDeviceLiveDataMap(@RequestParam(value = "userId", defaultValue = "0") Long userId){
        try{
            return ResponseEntity.ok(
                    deviceServiceImpl.getDeviceLiveDataMap(userId)
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

    @GetMapping(path = "/getDeviceById")
    public ResponseEntity<ApiResponse<Device>> getDeviceById(@RequestParam(value = "userId", defaultValue = "0") Long userId,
                                                             @RequestParam(value = "deviceId", defaultValue = "0")Long deviceId){
        try{
            return ResponseEntity.ok(
                    deviceServiceImpl.getDeviceById(userId, deviceId)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "/getDeviceDriver")
    public ResponseEntity<ApiResponse<Driver>> getDeviceDriver(@RequestParam(value = "userId", defaultValue = "0") Long userId,
                                                               @RequestParam(value = "deviceId", defaultValue = "0")Long deviceId){
        try{
            return ResponseEntity.ok(
                    deviceServiceImpl.getDeviceDriver(userId, deviceId)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @PutMapping(path="/changeIcon")
    public ResponseEntity<ApiResponse<DeviceResponse>>changeIcon(@RequestParam (value = "userId", defaultValue = "0") Long userId, @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId, @RequestParam(value = "icon", defaultValue = "") String icon){
        try {
            return ResponseEntity.ok(deviceServiceImpl.changeIcon(deviceId,userId,icon));
        }catch (Exception |Error e){

            throw new ApiRequestException(e.getLocalizedMessage());
        }

    }

    @GetMapping(path="/getDeviceSelect")
    public ResponseEntity<ApiResponse<List<GetDeviceSelectResponse>>>getDeviceSelect(@RequestParam (value = "userId", defaultValue = "0") Long userId){
        try {
            return ResponseEntity.ok(deviceServiceImpl.getDeviceSelect(userId));
        }catch (Exception |Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }

    }

}
