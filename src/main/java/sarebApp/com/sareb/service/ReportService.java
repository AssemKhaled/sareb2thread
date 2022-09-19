package sarebApp.com.sareb.service;

import org.springframework.http.ResponseEntity;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.*;

import java.util.List;

/**
 * @author Assem
 */
public interface ReportService {


    ApiResponse<List<StopReportResponse>> getStopsReport(Long [] deviceId, String type, String from,String to
            ,int page,int start,int limit,Long userId,String timeOffset);

    ApiResponse<List<TripReportResponse>> getTripsReport(Long [] deviceId, String type, String from, String to
             , int page, int start, int limit, Long userId, String timeOffset);

    ApiResponse<List<SummaryReportResponse>> getSummaryReport(Long [] deviceId, String type, String from
            ,String to, int page, int start, int limit, Long userId, String timeOffset);

    ApiResponse<List<TripViewPositions>> getViewTrip(Long deviceId,String from,String to);

//    ApiResponse<List<EventReportTraccarResponse>> getEventsReportTraccar(Long [] deviceId, String type, String from
//            , String to, int page, int start, int limit, Long userId);

    ApiResponse<List<EventReportResponse>> getEventsReport(Long [] deviceId, int offset, String start
            , String end, String type, String search, Long userId, String exportData, String timeOffset);


}
