package sarebApp.com.sareb.service;

import org.springframework.http.ResponseEntity;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.DistanceFuelEngineResponse;
import sarebApp.com.sareb.dto.responses.GetIgnitionChartResponse;
import sarebApp.com.sareb.dto.responses.GetStatusDevices;
import sarebApp.com.sareb.dto.responses.MergeHoursIgnitionResponse;

import java.util.List;

public interface ChartService {
    ApiResponse<GetIgnitionChartResponse>getIgnitionChart(Long userId, List<Long>deviceId);
    ApiResponse<List<MergeHoursIgnitionResponse>>getMergeHoursIgnitionByFilter(List<Long>deviceId,Long userId);

    ApiResponse<GetStatusDevices>getStatusByFilter(List<Long>deviceId, Long userId);

    ApiResponse<List<DistanceFuelEngineResponse>>getDistanceFuelEngineByFilter(List<Long>deviceId,Long userId);

}
