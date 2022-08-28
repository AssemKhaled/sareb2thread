package sarebApp.com.sareb.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.DistanceFuelEngineResponse;
import sarebApp.com.sareb.dto.responses.MergeHoursIgnitionResponse;
import sarebApp.com.sareb.exception.ApiRequestException;
import sarebApp.com.sareb.service.Impl.ChartServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/app/dashboard")
public class ChartController {
    private final ChartServiceImpl chartService;

    public ChartController(ChartServiceImpl chartService) {
        this.chartService = chartService;
    }
    @GetMapping(path = "/getDistanceFuelEngine")
    public ResponseEntity<ApiResponse<List<DistanceFuelEngineResponse>>> getDistanceFuelEngine(@RequestParam(value = "userId", defaultValue = "0") Long userId){
        try{
            return ResponseEntity.ok(
                    chartService.getDistanceFuelEngine(userId)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }
    @GetMapping(path = "/getMergeHoursIgnition")
    public ResponseEntity<ApiResponse<List<MergeHoursIgnitionResponse>>>getMergeHoursIgnition(@RequestParam(value = "userId", defaultValue = "0") Long userId){
        try{
            return ResponseEntity.ok(
                    chartService.getMergeHoursIgnition(userId)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }
}
