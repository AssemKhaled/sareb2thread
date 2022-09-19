package sarebApp.com.sareb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.*;
import sarebApp.com.sareb.exception.ApiRequestException;
import sarebApp.com.sareb.service.Impl.ReportServiceImpl;

import java.util.List;

/**
 * @author Assem
 */
@CrossOrigin
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportServiceImpl reportServiceImpl;

    @GetMapping(path = "/getStopsReport")
    public ResponseEntity<ApiResponse<List<StopReportResponse>>> getStopsReport(@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
                                                                                @RequestParam (value = "type", defaultValue = "allEvents") String type,
                                                                                @RequestParam (value = "from", defaultValue = "0") String from,
                                                                                @RequestParam (value = "to", defaultValue = "0") String to,
                                                                                @RequestParam (value = "page", defaultValue = "1") int page,
                                                                                @RequestParam (value = "start", defaultValue = "0") int start,
                                                                                @RequestParam (value = "limit", defaultValue = "10") int limit,
                                                                                @RequestParam (value = "userId",defaultValue = "0")Long userId,
                                                                                @RequestParam (value = "timeOffset",defaultValue = "")String timeOffset) {
        try{
            return ResponseEntity.ok(
                    reportServiceImpl.getStopsReport(deviceId, type, from, to, page, start, limit,userId,timeOffset)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "/getTripsReport")
    public ResponseEntity<ApiResponse<List<TripReportResponse>>> getTripsReport(@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
                                                                                @RequestParam (value = "type", defaultValue = "allEvents") String type,
                                                                                @RequestParam (value = "from", defaultValue = "0") String from,
                                                                                @RequestParam (value = "to", defaultValue = "0") String to,
                                                                                @RequestParam (value = "page", defaultValue = "1") int page,
                                                                                @RequestParam (value = "start", defaultValue = "0") int start,
                                                                                @RequestParam (value = "limit", defaultValue = "10") int limit,
                                                                                @RequestParam (value = "userId",defaultValue = "0")Long userId,
                                                                                @RequestParam (value = "timeOffset",defaultValue = "")String timeOffset) {
        try{
            return ResponseEntity.ok(
                    reportServiceImpl.getTripsReport(deviceId, type, from, to, page, start, limit,userId,timeOffset)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }
    @GetMapping(path = "/getViewTrip")
    public ResponseEntity<ApiResponse<List<TripViewPositions>>> getViewTrip(@RequestParam (value = "deviceId", defaultValue = "") Long deviceId,
                                                                            @RequestParam (value = "from", defaultValue = "") String from,
                                                                            @RequestParam (value = "to", defaultValue = "") String to) {
        try{
            return ResponseEntity.ok(
                    reportServiceImpl.getViewTrip(deviceId,from,to)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "/getSummaryReport")
    public ResponseEntity<ApiResponse<List<SummaryReportResponse>>> getSummaryReport(@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
                                                                                     @RequestParam (value = "type", defaultValue = "allEvents") String type,
                                                                                     @RequestParam (value = "from", defaultValue = "0") String from,
                                                                                     @RequestParam (value = "to", defaultValue = "0") String to,
                                                                                     @RequestParam (value = "page", defaultValue = "1") int page,
                                                                                     @RequestParam (value = "start", defaultValue = "0") int start,
                                                                                     @RequestParam (value = "limit", defaultValue = "10") int limit,
                                                                                     @RequestParam (value = "userId",defaultValue = "0")Long userId,
                                                                                     @RequestParam (value = "timeOffset",defaultValue = "")String timeOffset) {
        try{
            return ResponseEntity.ok(
                    reportServiceImpl.getSummaryReport(deviceId, type, from, to, page, start, limit,userId,timeOffset)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "/getEventsReport")
    public ResponseEntity<ApiResponse<List<EventReportResponse>>> getEventsReport(@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
                                                                                                @RequestParam (value = "offset", defaultValue = "0") int offset,
                                                                                                @RequestParam (value = "from", defaultValue = "0") String start,
                                                                                                @RequestParam (value = "exportData", defaultValue = "") String exportData,
                                                                                                @RequestParam (value = "to", defaultValue = "0") String end,
                                                                                                @RequestParam (value = "type", defaultValue = "") String type,
                                                                                                @RequestParam (value = "search", defaultValue = "") String search,
                                                                                                @RequestParam (value = "userId",defaultValue = "0")Long userId,
                                                                                                @RequestParam (value = "timeOffset",defaultValue = "")String timeOffset ) {
        try{
            return ResponseEntity.ok(
                    reportServiceImpl.getEventsReport(deviceId,offset, start, end, type, search, userId,exportData,timeOffset)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

}
