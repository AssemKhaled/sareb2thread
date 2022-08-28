package sarebApp.com.sareb.service;

import org.springframework.http.ResponseEntity;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.DistanceFuelEngineResponse;
import sarebApp.com.sareb.dto.responses.MergeHoursIgnitionResponse;

import java.util.List;

public interface ChartService {
    ApiResponse<List<DistanceFuelEngineResponse>>getDistanceFuelEngine(Long userId);
    ApiResponse<List<MergeHoursIgnitionResponse>>getMergeHoursIgnition(Long userId);
}
