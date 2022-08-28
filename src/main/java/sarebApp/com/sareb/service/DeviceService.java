package sarebApp.com.sareb.service;

import org.springframework.http.ResponseEntity;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.*;
import sarebApp.com.sareb.entities.Device;
import sarebApp.com.sareb.entities.Driver;

import java.util.List;

/**
 * @author Assem
 */
public interface DeviceService {

    ApiResponse<List<DevicesMapResponse>> getDeviceLiveDataMap(Long userId);
    ApiResponse<VehicleDetailsResponse> getDeviceDetails(Long userId,Long deviceId);
    ApiResponse<List<VehicleDetailsResponse>> getDevicesList(Long userId,int offset, String search);
    List<Device> searchDevice(int offset, String search,List<Long>ids,int type);
    ApiResponse<Device> getDeviceById(Long userId, Long deviceId);
    ApiResponse<Driver> getDeviceDriver(Long userId, Long deviceId);
    ApiResponse<List<GetDeviceSelectResponse>>getDeviceSelect(Long userId);
    ApiResponse<GetStatusDevices>getStatus(Long userId);
    ApiResponse<DeviceResponse>changeIcon(Long deviceId, Long userId, String icon);


}
