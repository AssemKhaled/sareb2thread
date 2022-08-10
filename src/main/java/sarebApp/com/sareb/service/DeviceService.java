package sarebApp.com.sareb.service;

import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.AllDeviceLiveDataResponse;

import java.util.List;

/**
 * @author Assem
 */
public interface DeviceService {

    ApiResponse<List<AllDeviceLiveDataResponse>> getAllDeviceDashBoard(Long userId, int offset, String search);

}
