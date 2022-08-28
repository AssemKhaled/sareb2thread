package sarebApp.com.sareb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.DriverSelectResponse;
import sarebApp.com.sareb.dto.responses.VehicleDetailsResponse;
import sarebApp.com.sareb.entities.Device;
import sarebApp.com.sareb.entities.Driver;
import sarebApp.com.sareb.exception.ApiRequestException;
import sarebApp.com.sareb.service.Impl.DriverServiceImpl;

import java.util.List;

/**
 * @author Assem
 */
@CrossOrigin
@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor

public class DriverController {

    private final DriverServiceImpl driverServiceImpl;

    @GetMapping(path = "/getDriverList")
    public ResponseEntity<ApiResponse<List<Driver>>> getDriverList(@RequestParam(value = "userId", defaultValue = "0") Long userId,
                                                                   @RequestParam(value = "offset", defaultValue = "0")int offset,
                                                                   @RequestParam(value = "search", defaultValue = "")String search){
        try{
            return ResponseEntity.ok(
                    driverServiceImpl.getDriverList(userId, offset,search)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "/getDriverById")
    public ResponseEntity<ApiResponse<Driver>> getDriverById(@RequestParam(value = "userId", defaultValue = "0") Long userId,
                                                             @RequestParam(value = "driverId", defaultValue = "0")Long driverId){
        try{
            return ResponseEntity.ok(
                    driverServiceImpl.getDriverById(userId, driverId)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "/getDriverSelect")
    public ResponseEntity<ApiResponse<List<DriverSelectResponse>>> getDriverSelect(@RequestParam(value = "userId", defaultValue = "0") Long userId){
        try{
            return ResponseEntity.ok(
                    driverServiceImpl.getDriverSelect(userId)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }


}
