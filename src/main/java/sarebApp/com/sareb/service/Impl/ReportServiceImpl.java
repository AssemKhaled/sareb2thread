package sarebApp.com.sareb.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.ApiResponseBuilder;
import sarebApp.com.sareb.dto.responses.*;
import sarebApp.com.sareb.entities.Device;
import sarebApp.com.sareb.entities.MongoEvents;
import sarebApp.com.sareb.entities.MongoPositions;
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.exception.ApiGetException;
import sarebApp.com.sareb.helper.Impl.ReportsHelper;
import sarebApp.com.sareb.repository.DeviceRepository;
import sarebApp.com.sareb.repository.MongoEventsRepository;
import sarebApp.com.sareb.repository.MongoPositionsRepository;
import sarebApp.com.sareb.repository.UserRepository;
import sarebApp.com.sareb.service.ReportService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Assem
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Value("${stopsUrl}")
    private String stopsUrl;
    @Value("${tripsUrl}")
    private String tripsUrl;
    @Value("${summaryUrl}")
    private String summaryUrl;
    @Value("${eventsUrl}")
    private String eventsUrl;

    private final UserRepository userRepository;
    private final ReportsHelper reportsHelper;
    private final MongoEventsRepository mongoEventsRepository;
    private final DeviceRepository deviceRepository;
    private final MongoPositionsRepository mongoPositionsRepository;

    @Override
    public ApiResponse<List<StopReportResponse>> getStopsReport(Long[] deviceId, String type,String from
            ,String to, int page, int start, int limit, Long userId, String timeOffset) {
        log.info("************************ GET STOPS REPORT STARTED ***************************");
        ApiResponseBuilder<List<StopReportResponse>> builder = new ApiResponseBuilder<>();
        List<StopReportResponse> result;List<Long> allDevices;Date dateFrom;Date dateTo;
        Optional<User> optionalUser = userRepository.findByIdAndDeleteDate(userId,null);
        StringBuilder appendString= new StringBuilder();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        inputFormat1.setLenient(false);inputFormat.setLenient(false);outputFormat.setLenient(false);

        if (optionalUser.isEmpty()) {
            builder.setEntity(null);
            builder.setMessage("User Not Found");
            builder.setSuccess(true);
            builder.setSize(0);
            builder.setStatusCode(200);
            return builder.build();
        }

        allDevices = Arrays.stream(deviceId).toList();

        if(from.equals("0") || to.equals("0")) {
                throw new ApiGetException("Date start and end is Required");

            }else {
                try {
                    dateFrom = inputFormat.parse(from);
                    from = outputFormat.format(dateFrom);
                    dateTo = inputFormat.parse(to);
                    to = outputFormat.format(dateTo);
                } catch (ParseException e2) {
                    try {
                        dateFrom = inputFormat1.parse(from);
                        from = outputFormat.format(dateFrom);
                        dateTo = inputFormat1.parse(to);
                        to = outputFormat.format(dateTo);
                    } catch (ParseException e) {
                        throw new ApiGetException("Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    }
                }
                if(dateFrom.getTime() > dateTo.getTime()) {
                    throw new ApiGetException("Start Date should be Earlier than End Date");
                }
                if(allDevices.size()>0) {
                    for (Long allDevice : allDevices) {
                        if (!appendString.toString().equals("")) {
                            appendString.append(",").append(allDevice);
                        } else {
                            appendString.append(allDevice);
                        }
                    }
                }
                allDevices = new ArrayList<>();

                String[] data = {};
                if(!appendString.toString().equals("")) {
                    data = appendString.toString().split(",");

                }
                for(String d:data) {
                    if(!allDevices.contains(Long.parseLong(d))) {
                        allDevices.add(Long.parseLong(d));
                    }
                }
                if(allDevices.isEmpty()) {
                    throw new ApiGetException("no data for devices of group or devices that you selected ");
                }
            }

        result = (List<StopReportResponse>) reportsHelper.returnFromTraccar(stopsUrl,"stops",allDevices, from, to, type, page, start, limit).getBody();

        assert result != null;
        if(result.size()>0) {
            result = reportsHelper.stopReportProcessHandler(result, timeOffset);
        }
        builder.setEntity(result);
        builder.setMessage("Success");
        builder.setSuccess(true);
        builder.setSize(result.size());
        builder.setStatusCode(200);
        log.info("************************ GET STOPS REPORT ENDED ***************************");
        return builder.build();
    }

    @Override
    public ApiResponse<List<TripReportResponse>> getTripsReport(Long[] deviceId, String type, String from
            ,String to, int page, int start, int limit, Long userId, String timeOffset) {
        log.info("************************ GET TRIPS REPORT STARTED ***************************");
        ApiResponseBuilder<List<TripReportResponse>> builder = new ApiResponseBuilder<>();
        List<TripReportResponse> result;List<Long> allDevices;Date dateFrom;Date dateTo;
        Optional<User> optionalUser = userRepository.findByIdAndDeleteDate(userId,null);
        StringBuilder appendString= new StringBuilder();
        SimpleDateFormat outputFormatFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormatTo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormatFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormatTo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        inputFormat1.setLenient(false);inputFormatFrom.setLenient(false);outputFormatFrom.setLenient(false);
        inputFormatTo.setLenient(false);outputFormatTo.setLenient(false);
        if (optionalUser.isEmpty()) {
            builder.setEntity(null);
            builder.setMessage("User Not Found");
            builder.setSuccess(true);
            builder.setSize(0);
            builder.setStatusCode(200);
            return builder.build();
        }

        allDevices = Arrays.stream(deviceId).toList();

        if(from.equals("0") || to.equals("0")) {
            throw new ApiGetException("Date start and end is Required");

        }else {
            try {
                dateFrom = inputFormatFrom.parse(from);
                from = outputFormatFrom.format(dateFrom);
                dateTo = inputFormatTo.parse(to);
                to = outputFormatTo.format(dateTo);
            } catch (ParseException e2) {
                try {
                    dateFrom = inputFormat1.parse(from);
                    from = outputFormatFrom.format(dateFrom);
                    dateTo = inputFormat2.parse(to);
                    to = outputFormatTo.format(dateTo);
                } catch (ParseException e) {
                    throw new ApiGetException("Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                }
            }
            if(dateFrom.getTime() > dateTo.getTime()) {
                throw new ApiGetException("Start Date should be Earlier than End Date");
            }
            if(allDevices.size()>0) {
                for (Long allDevice : allDevices) {
                    if (!appendString.toString().equals("")) {
                        appendString.append(",").append(allDevice);
                    } else {
                        appendString.append(allDevice);
                    }
                }
            }
            allDevices = new ArrayList<>();

            String[] data = {};
            if(!appendString.toString().equals("")) {
                data = appendString.toString().split(",");

            }
            for(String d:data) {
                if(!allDevices.contains(Long.parseLong(d))) {
                    allDevices.add(Long.parseLong(d));
                }
            }
            if(allDevices.isEmpty()) {
                throw new ApiGetException("no data for devices of group or devices that you selected ");
            }
        }
        log.info("STARDTEDDDD????");
        result = (List<TripReportResponse>) reportsHelper.returnFromTraccar(tripsUrl,"trips",allDevices, from, to, type, page, start, limit).getBody();
        log.info("ENDEDDDD??????");
        assert result != null;
        if(result.size()>0) {
            result = reportsHelper.tripReportProcessHandler(result, timeOffset);
        }
        builder.setEntity(result);
        builder.setMessage("Success");
        builder.setSuccess(true);
        builder.setSize(result.size());
        builder.setStatusCode(200);
        log.info("************************ GET TRIPS REPORT ENDED ***************************");
        return builder.build();
    }

    @Override
    public ApiResponse<List<SummaryReportResponse>> getSummaryReport(Long[] deviceId, String type, String from, String to, int page, int start, int limit, Long userId, String timeOffset) {
        log.info("************************ GET SUMMARY REPORT STARTED ***************************");
        ApiResponseBuilder<List<SummaryReportResponse>> builder = new ApiResponseBuilder<>();
        List<SummaryReportResponse> result;List<Long> allDevices;Date dateFrom;Date dateTo;
        Optional<User> optionalUser = userRepository.findByIdAndDeleteDate(userId,null);
        StringBuilder appendString= new StringBuilder();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        inputFormat1.setLenient(false);inputFormat.setLenient(false);outputFormat.setLenient(false);

        if (optionalUser.isEmpty()) {
            builder.setEntity(null);
            builder.setMessage("User Not Found");
            builder.setSuccess(true);
            builder.setSize(0);
            builder.setStatusCode(200);
            return builder.build();
        }

        allDevices = Arrays.stream(deviceId).toList();

        if(from.equals("0") || to.equals("0")) {
            throw new ApiGetException("Date start and end is Required");

        }else {
            try {
                dateFrom = inputFormat.parse(from);
                from = outputFormat.format(dateFrom);
                dateTo = inputFormat.parse(to);
                to = outputFormat.format(dateTo);
            } catch (ParseException e2) {
                try {
                    dateFrom = inputFormat1.parse(from);
                    from = outputFormat.format(dateFrom);
                    dateTo = inputFormat1.parse(to);
                    to = outputFormat.format(dateTo);
                } catch (ParseException e) {
                    throw new ApiGetException("Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                }
            }
            if(dateFrom.getTime() > dateTo.getTime()) {
                throw new ApiGetException("Start Date should be Earlier than End Date");
            }
            if(allDevices.size()>0) {
                for (Long allDevice : allDevices) {
                    if (!appendString.toString().equals("")) {
                        appendString.append(",").append(allDevice);
                    } else {
                        appendString.append(allDevice);
                    }
                }
            }
            allDevices = new ArrayList<>();

            String[] data = {};
            if(!appendString.toString().equals("")) {
                data = appendString.toString().split(",");

            }
            for(String d:data) {
                if(!allDevices.contains(Long.parseLong(d))) {
                    allDevices.add(Long.parseLong(d));
                }
            }
            if(allDevices.isEmpty()) {
                throw new ApiGetException("no data for devices of group or devices that you selected ");
            }
        }

        result = (List<SummaryReportResponse>) reportsHelper.returnFromTraccar(summaryUrl,"summary",allDevices, from, to, type, page, start, limit).getBody();

        assert result != null;
        if(result.size()>0) {
            result = reportsHelper.summaryReportProcessHandler(result, timeOffset);
        }
        builder.setEntity(result);
        builder.setMessage("Success");
        builder.setSuccess(true);
        builder.setSize(result.size());
        builder.setStatusCode(200);
        log.info("************************ GET SUMMARY REPORT ENDED ***************************");
        return builder.build();
    }

    @Override
    public ApiResponse<List<TripViewPositions>> getViewTrip(Long deviceId, String from, String to) {
        log.info("************************ GET VIEW TRIP STARTED ***************************");
        ApiResponseBuilder<List<TripViewPositions>> builder = new ApiResponseBuilder<>();
        List<TripViewPositions> result = new ArrayList<>();
        Date dateFrom;Date dateTo;

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS SSSS");
        SimpleDateFormat inputFormat1 = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        inputFormat1.setLenient(false);inputFormat.setLenient(false);outputFormat.setLenient(false);

        try {
            dateFrom = inputFormat2.parse(from);
            from = outputFormat.format(dateFrom);
            dateTo = inputFormat2.parse(to);
            to = outputFormat.format(dateTo);

        } catch (ParseException e2) {
            try {
                dateFrom = sdf.parse(from);
                from = outputFormat.format(dateFrom);
                dateTo = sdf.parse(to);
                to = outputFormat.format(dateTo);

            } catch (ParseException e3) {
                // TODO Auto-generated catch block
                try {
                    dateFrom = inputFormat1.parse(from);
                    from = outputFormat.format(dateFrom);
                    dateTo = inputFormat1.parse(to);
                    to = outputFormat.format(dateTo);
                } catch (ParseException e4) {
                    try {
                        dateFrom=inputFormat.parse(from);
                        from=outputFormat.format(dateFrom);
                        dateTo=inputFormat.parse(to);
                        to=outputFormat.format(dateTo);
                    }catch (ParseException e){
                        throw new ApiGetException("Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    }
                }
            }
        }

        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isPresent()){
            List<MongoPositions> mongoPositionsList = mongoPositionsRepository.findTrips(deviceId,dateFrom,dateTo);
            for (MongoPositions positions:mongoPositionsList) {
               result.add(
                  TripViewPositions
                          .builder()
                          .lat(positions.getLatitude())
                          .lon(positions.getLongitude())
                          .course(positions.getCourse())
                          .build()
               ) ;
            }
            log.info("************************ GET VIEW TRIP ENDED ***************************");
            builder.setEntity(result);
            builder.setSize(result.size());
            builder.setStatusCode(200);
            builder.setMessage("Success");
            builder.setSuccess(true);
            return builder.build();

        }else {
            throw new ApiGetException("This Device IS NOT FOUND");
        }

    }


    @Override
    public ApiResponse<List<EventReportResponse>> getEventsReport(Long[] deviceId, int offset, String start, String end, String type, String search, Long userId, String exportData, String timeOffset) {
        log.info("************************ GET EVENTS REPORT STARTED ***************************");
        ApiResponseBuilder<List<EventReportResponse>> builder = new ApiResponseBuilder<>();
        List<EventReportResponse> result;List<Long> allDevices;Date dateFrom;Date dateTo;
        Optional<User> optionalUser = userRepository.findByIdAndDeleteDate(userId,null);
        StringBuilder appendString= new StringBuilder(); Integer size = 0;
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        inputFormat1.setLenient(false);inputFormat.setLenient(false);outputFormat.setLenient(false);

        if (optionalUser.isEmpty()) {
            builder.setEntity(null);
            builder.setMessage("User Not Found");
            builder.setSuccess(true);
            builder.setSize(0);
            builder.setStatusCode(200);
            return builder.build();
        }

        allDevices = Arrays.stream(deviceId).toList();

        if(start.equals("0") || end.equals("0")) {
            throw new ApiGetException("Date start and end is Required");

        }else {
            try {
                dateFrom = inputFormat.parse(start);
                start = outputFormat.format(dateFrom);
                dateTo = inputFormat.parse(end);
                end = outputFormat.format(dateTo);
            } catch (ParseException e2) {
                try {
                    dateFrom = inputFormat1.parse(start);
                    start = outputFormat.format(dateFrom);
                    dateTo = inputFormat1.parse(end);
                    end = outputFormat.format(dateTo);
                } catch (ParseException e) {
                    throw new ApiGetException("Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                }
            }
            if(dateFrom.getTime() > dateTo.getTime()) {
                throw new ApiGetException("Start Date should be Earlier than End Date");
            }
            if(allDevices.size()>0) {
                for (Long allDevice : allDevices) {
                    if (!appendString.toString().equals("")) {
                        appendString.append(",").append(allDevice);
                    } else {
                        appendString.append(allDevice);
                    }
                }
            }
            allDevices = new ArrayList<>();

            String[] data = {};
            if(!appendString.toString().equals("")) {
                data = appendString.toString().split(",");

            }
            for(String d:data) {
                if(!allDevices.contains(Long.parseLong(d))) {
                    allDevices.add(Long.parseLong(d));
                }
            }
            if(allDevices.isEmpty()) {
                throw new ApiGetException("no data for devices of group or devices that you selected ");
            }
        }

        List<MongoEvents> mongoEventsList = new ArrayList<>();
        int limit = 10;
        Pageable pageable = PageRequest.of(offset, limit);

        if(type.equals("")) {

            if(exportData.equals("exportData")) {
//					eventReport = mongoEventsRepo.getEventsScheduled(allDevices, dateFrom, dateTo);
                mongoEventsList = mongoEventsRepository.
                        findEvents(allDevices, dateFrom, dateTo);

                result = reportsHelper.eventsReportProcessHandler(mongoEventsList, timeOffset);
                builder.setEntity(result);
                builder.setMessage("Success");
                builder.setSuccess(true);
                builder.setSize(result.size());
                builder.setStatusCode(200);
                log.info("************************ getEventsReport ENDED ***************************");
                return builder.build();
            }else {
                //					eventReport = mongoEventsRepo.getEventsWithoutType(allDevices, offset, dateFrom, dateTo);
//                mongoEventsList = mongoEventsRepository.
//                        findAllByDeviceidInAndServertimeBetweenOrderByServertimeDesc(
//                                allDevices, dateFrom, dateTo, pageable);
                mongoEventsList = mongoEventsRepository.
                        findEventsPage(allDevices, dateFrom, dateTo,pageable);
                result = reportsHelper.eventsReportProcessHandler(mongoEventsList, timeOffset);
                if(result.size()>0) {
//						size = mongoEventsRepo.getEventsWithoutTypeSize(allDevices, dateFrom, dateTo);
//                    size = mongoEventsRepository.
//                            countAllByDeviceidInAndServertimeBetween(allDevices, dateFrom, dateTo);
                    size = result.size();
                }
            }
        } else {
            if(exportData.equals("exportData")) {
//					eventReport = mongoEventsRepo.getEventsScheduledWithType(allDevices, dateFrom, dateTo,type);
                mongoEventsList = mongoEventsRepository.
                        findAllByDeviceidInAndServertimeBetweenAndTypeOrderByServertimeDesc(
                                allDevices, dateFrom, dateTo, type);

                result = reportsHelper.eventsReportProcessHandler(mongoEventsList, timeOffset);
                builder.setEntity(result);
                builder.setMessage("Success");
                builder.setSuccess(true);
                builder.setSize(result.size());
                builder.setStatusCode(200);
                log.info("************************ getEventsReport ENDED ***************************");
                return builder.build();
            }else {
                //					eventReport = mongoEventsRepo.getEventsWithType(allDevices, offset, dateFrom, dateTo, type);
//                mongoEventsList = mongoEventsRepository.
//                        findAllByDeviceidInAndServertimeBetweenAndTypeOrderByServertimeDesc(
//                                allDevices, dateFrom, dateTo, type, pageable);
                mongoEventsList = mongoEventsRepository.
                        findEventsPage(allDevices, dateFrom, dateTo,pageable);
                result = reportsHelper.eventsReportProcessHandler(mongoEventsList, timeOffset);
                if(result.size()>0) {
//						size = mongoEventsRepo.getEventsWithTypeSize(allDevices, dateFrom, dateTo, type);
//                    size = mongoEventsRepository.
//                            countAllByDeviceidInAndServertimeBetweenAndType(allDevices, dateFrom, dateTo, type);
                    size = result.size();
              }
            }
        }
        builder.setEntity(result);
        builder.setMessage("Success");
        builder.setSuccess(true);
        builder.setSize(size);
        builder.setStatusCode(200);
        log.info("************************ GET EVENTS REPORT ENDED ***************************");
        return builder.build();
    }


}
