package sarebApp.com.sareb.service;

import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.AllDeviceLiveDataResponse;
import sarebApp.com.sareb.dto.responses.VehicleDetailsResponse;
import sarebApp.com.sareb.entities.Device;

import java.util.List;

/**
 * @author Assem
 */
public interface DeviceService {

   ApiResponse<?> createDevice(Device device, Long userId);
    ApiResponse<List<AllDeviceLiveDataResponse>> getAllDevicesDashBoard(Long userId, int offset, String search);
    ApiResponse<VehicleDetailsResponse> getDeviceDetails(Long userId,Long deviceId);
    ApiResponse<List<VehicleDetailsResponse>> getDevicesList(Long userId,int offset, String search);

}
