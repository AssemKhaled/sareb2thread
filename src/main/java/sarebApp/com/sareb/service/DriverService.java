package sarebApp.com.sareb.service;

import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.DriverSelectResponse;
import sarebApp.com.sareb.entities.Device;
import sarebApp.com.sareb.entities.Driver;

import java.util.List;

/**
 * @author Assem
 */
public interface DriverService {

    ApiResponse<List<Driver>> getDriverList(Long userId, int offset, String search);
    ApiResponse<Driver> getDriverById(Long userId, Long driverId);
    ApiResponse<List<DriverSelectResponse>> getDriverSelect(Long userId);
    List<Driver> searchDriver(int offset, String search,List<Long>ids,int type);
}
