package sarebApp.com.sareb.helper.Impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import sarebApp.com.sareb.dto.responses.*;
import sarebApp.com.sareb.entities.*;
import sarebApp.com.sareb.repository.DeviceRepository;
import sarebApp.com.sareb.repository.DriverRepository;
import sarebApp.com.sareb.repository.MongoPositionsRepository;
import sarebApp.com.sareb.service.Impl.DeviceServiceImpl;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsHelper {

    private final DeviceServiceImpl deviceServiceImpl;
    private final DeviceRepository deviceRepository;
    private final DriverRepository driverRepository;
    private final Utilities utilities;
    private final MongoPositionsRepository mongoPositionsRepository;

    public ResponseEntity<?> returnFromTraccar(String url, String report, List<Long> allDevices, String from
            , String to, String type, int page, int start, int limit) {

        log.info("************************ returnFromTraccar STARTED ***************************");

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        outputFormat.setLenient(false);
        Date dateFrom; Date dateTo;
        try {
            dateFrom = outputFormat.parse(from);
            Calendar calendarFrom = Calendar.getInstance();
            calendarFrom.setTime(dateFrom);
//            calendarFrom.add(Calendar.HOUR_OF_DAY, -3);
            dateFrom = calendarFrom.getTime();
            from = outputFormat.format(dateFrom);

            dateTo = outputFormat.parse(to);
            Calendar calendarTo = Calendar.getInstance();
            calendarTo.setTime(dateTo);
//            calendarTo.add(Calendar.HOUR_OF_DAY, -3);
            dateTo = calendarTo.getTime();
            to = outputFormat.format(dateTo);

        } catch (ParseException ignored) {

        }


        String plainCreds = "admin@fuinco.com:admin";
        byte[] plainCredsBytes = plainCreds.getBytes();

        byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("type", type)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("page", page)
                .queryParam("start", start)
                .queryParam("limit",limit).build();
        HttpEntity<String> request = new HttpEntity<>(headers);
        StringBuilder URL = new StringBuilder(builder.toString());
        if(allDevices.size()>0) {
            for (Long allDevice : allDevices) {
                URL.append("&deviceId=").append(allDevice);
            }
        }
        if(report.equals("stops")) {
            ResponseEntity<List<StopReportResponse>> rateResponse =
                    restTemplate.exchange(URL.toString(),
                            HttpMethod.GET,request, new ParameterizedTypeReference<>() {
                            });
            log.info("************************ returnFromTraccar StopReport ENDED ***************************");

            return rateResponse;
        }
        if(report.equals("trips")) {
            ResponseEntity<List<TripReportResponse>> rateResponse =
                    restTemplate.exchange(URL.toString(),
                            HttpMethod.GET,request, new ParameterizedTypeReference<>() {
                            });
            log.info("************************ returnFromTraccar TripReport ENDED ***************************");

            return rateResponse;
        }
        if(report.equals("summary")) {
            ResponseEntity<List<SummaryReportResponse>> rateResponse =
                    restTemplate.exchange(URL.toString(),
                            HttpMethod.GET,request, new ParameterizedTypeReference<>() {
                            });
            log.info("************************ returnFromTraccar SummaryReport ENDED ***************************");

            return rateResponse;
        }
        if(report.equals("events")) {
            ResponseEntity<List<EventReportTraccarResponse>> rateResponse =
                    restTemplate.exchange(URL.toString(),
                            HttpMethod.GET,request, new ParameterizedTypeReference<>() {
                            });
            log.info("************************ returnFromTraccar EventReport ENDED ***************************");

            return rateResponse;
        }


        log.info("************************ returnFromTraccar ENDED ***************************");
        return null;
    }

    public List<TripReportResponse> tripReportProcessHandler(List<TripReportResponse> tripReports,String timeOffset){
        List<Long> deviceIds ;List<Long> driverIds;
        deviceIds = tripReports.stream().map(TripReportResponse::getDeviceId).collect(Collectors.toList());
        Optional<List<Device>> optionalDeviceList= deviceRepository.findAllByIdInAndDeleteDate(deviceIds,null);
        List<Device> deviceList = optionalDeviceList.get();
        driverIds = deviceList.stream().map(Device::getDriverId).filter(Objects::nonNull).collect(Collectors.toList());
        Optional<List<Driver>> optionalDriverList = driverRepository.findAllByIdIn(driverIds);
        List<Driver> driverList = optionalDriverList.get();
        for(TripReportResponse tripReport : tripReports ) {

            double totalDistance = 0.0 ; Double hum = null; Double temp = null;
            double roundOffDistance = 0.0;
            double roundOffFuel = 0.0;
            double litres=10.0; String deviceFuel = null;
            double Fuel =0.0;
            double distance=0.0;
            Long driverId;String driverName = null; String uniqueId = null;Set<User> companies = new HashSet<>();
            try {
                List<Device> devices= deviceList.stream().filter(device1 -> device1.getId().equals(tripReport.getDeviceId())).toList();

                if (!devices.isEmpty()){
                    driverId = devices.get(0).getDriverId();
                    if (driverId != null) {
                        driverName = driverList.stream().filter(driver -> driver.getId().equals(driverId)).findFirst().get().getName();
                        uniqueId = driverList.stream().filter(driver -> driver.getId().equals(driverId)).findFirst().get().getUniqueid();
                    }
                    deviceFuel = devices.get(0).getFuel();
                    companies = devices.get(0).getUser();
                    hum = devices.get(0).getLastHum();
                    temp = devices.get(0).getLastTemp();
                }

                tripReport.setHum(hum);
                tripReport.setTemp(temp);
                tripReport.setDriverName(driverName);
                tripReport.setDriverUniqueId(uniqueId);

            }catch (Exception | Error ignored){

            }

            User user = new User();
            for(User company : companies) {
                user = company;
                break;
            }
//            tripReport.setHum(device.getLastHum());
//            tripReport.setTemp(device.getLastTemp());
//            tripReport.setCompanyName(user.getName());
            tripReport.setCompanyName(user.getName());



            if(tripReport.getDistance() != null && !Objects.equals(tripReport.getDistance(), "")) {
                totalDistance = Math.abs(  Double.parseDouble(tripReport.getDistance())/1000  );
                roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
                tripReport.setDistance(Double.toString(roundOffDistance));
            }
            if(deviceFuel != null) {
                if(!Objects.equals(deviceFuel, "") && deviceFuel.startsWith("{")) {
                    JSONObject obj = new JSONObject(deviceFuel);
                    if(obj.has("fuelPerKM")) {
                        litres=obj.getDouble("fuelPerKM");

                    }
                }
            }
            distance = Double.parseDouble(tripReport.getDistance());
            if(distance > 0) {
                Fuel = (distance*litres)/100;
            }
            roundOffFuel = Math.round(Fuel * 100.0)/ 100.0;
            tripReport.setSpentFuel(Double.toString(roundOffFuel));

            if(tripReport.getDuration() != null && !Objects.equals(tripReport.getDuration(), "")) {
                tripReport.setDuration(utilities.durationCalculation(tripReport.getDuration()));
            }

            if(tripReport.getAverageSpeed() != null && !Objects.equals(tripReport.getAverageSpeed(), "")) {
                tripReport.setAverageSpeed(
                        String.valueOf(
                                utilities.speedConverter(Double.parseDouble(tripReport.getAverageSpeed()))
                        ));
            }
            if(tripReport.getMaxSpeed() != null && !Objects.equals(tripReport.getMaxSpeed(), "")) {
                tripReport.setMaxSpeed(String.valueOf(
                        utilities.speedConverter(Double.parseDouble(tripReport.getMaxSpeed()))
                ));
            }

            if(tripReport.getStartTime() != null && !Objects.equals(tripReport.getStartTime(), "")) {

                    Date dateTime = new Date();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

                    try {
//                        TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
//                        inputFormat.setTimeZone(etTimeZone);
                        dateTime = inputFormat.parse(tripReport.getStartTime());

                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                    if (dateTime !=null){
                        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        tripReport.setStartTime(utilities.timeZoneConverter(dateTime,timeOffset));
                    }
                    else {
                        tripReport.setStartTime(outputFormat.format(dateTime));
                    }
            }
            if(tripReport.getEndTime() != null && !Objects.equals(tripReport.getEndTime(), "")) {
                Date dateTime = null;
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
//                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

                try {
//                    TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
//                    inputFormat.setTimeZone(etTimeZone);
                    dateTime = inputFormat.parse(tripReport.getEndTime());
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(dateTime != null){
                    tripReport.setEndTime(utilities.timeZoneConverter(dateTime, timeOffset));
                }
                else {
                    tripReport.setEndTime(outputFormat.format(null));
                }
            }
        }
        return tripReports;
    }

    public List<StopReportResponse> stopReportProcessHandler(List<StopReportResponse> stopReports, String timeOffset){

        List<Long> deviceIds ; List<StopReportResponse> result = new ArrayList<>();List<Long> driverIds;
        deviceIds = stopReports.stream().map(StopReportResponse::getDeviceId).collect(Collectors.toList());
        Optional<List<Device>> optionalDeviceList= deviceRepository.findAllByIdInAndDeleteDate(deviceIds,null);
        List<Device> deviceList = optionalDeviceList.get();
        driverIds = deviceList.stream().map(Device::getDriverId).filter(Objects::nonNull).collect(Collectors.toList());
        Optional<List<Driver>> optionalDriverList = driverRepository.findAllByIdIn(driverIds);
        List<Driver> driverList = optionalDriverList.get(); Boolean deviceToDriver;
        for(StopReportResponse stopReportOne : stopReports ) {
//            Set<Driver>  drivers = device.get().getDriver();
            Long driverId;String driverName = null; String uniqueId = null;
            try {
                List<Device> devices= deviceList.stream().filter(device1 -> device1.getId().equals(stopReportOne.getDeviceId())).toList();
                if (!devices.isEmpty()){
                    driverId = devices.get(0).getDriverId();
                    if (driverId != null) {
                        driverName = driverList.stream().filter(driver -> driver.getId().equals(driverId)).findFirst().get().getName();
                        uniqueId = driverList.stream().filter(driver -> driver.getId().equals(driverId)).findFirst().get().getUniqueid();;
                    }
                }
                stopReportOne.setDriverName(driverName);
                stopReportOne.setDriverUniqueId(uniqueId);

            }catch (Exception | Error ignored){

            }


            if(stopReportOne.getDuration() != null && !Objects.equals(stopReportOne.getDuration(), "")) {
                stopReportOne.setDuration(utilities.durationCalculation(stopReportOne.getDuration()));
            }

            if(stopReportOne.getEngineHours() != null && !Objects.equals(stopReportOne.getEngineHours(), "")) {
                stopReportOne.setEngineHours(utilities.durationCalculation(stopReportOne.getEngineHours()));
            }

            if(stopReportOne.getStartTime() != null && !Objects.equals(stopReportOne.getStartTime(), "")) {
                Date dateTime = null;
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

                try {
//                    TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
//                    inputFormat.setTimeZone(etTimeZone);
                    dateTime = inputFormat.parse(stopReportOne.getStartTime());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(dateTime != null){
                    stopReportOne.setStartTime(utilities.timeZoneConverter(dateTime, timeOffset));
                }
                else {
                    stopReportOne.setStartTime(outputFormat.format(null));
                }
            }
            if(stopReportOne.getEndTime() != null && !Objects.equals(stopReportOne.getEndTime(), "")) {
                Date dateTime = null;
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

                try {
//                    TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
//                    inputFormat.setTimeZone(etTimeZone);
                    dateTime = inputFormat.parse(stopReportOne.getEndTime());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(dateTime != null){
                    stopReportOne.setEndTime(utilities.timeZoneConverter(dateTime, timeOffset));
                }
                else {
                    stopReportOne.setEndTime(outputFormat.format(null));
                }
            }
        }

        return stopReports;
    }

    public List<SummaryReportResponse> summaryReportProcessHandler(List<SummaryReportResponse> summaryReports, String timeOffset){
        double totalDistance = 0.0 ;
        double roundOffDistance = 0.0;
        double roundOffFuel = 0.0;
        double litres=10.0;
        double Fuel =0.0;
        double distance=0.0;
        List<Long> deviceIds ; List<SummaryReportResponse> result = new ArrayList<>();List<Long> driverIds;
        deviceIds = summaryReports.stream().map(SummaryReportResponse::getDeviceId).collect(Collectors.toList());
        Optional<List<Device>> optionalDeviceList= deviceRepository.findAllByIdInAndDeleteDate(deviceIds,null);
        List<Device> deviceList = optionalDeviceList.get();
        driverIds = deviceList.stream().map(Device::getDriverId).filter(Objects::nonNull).collect(Collectors.toList());
        Optional<List<Driver>> optionalDriverList = driverRepository.findAllByIdIn(driverIds);
        List<Driver> driverList = optionalDriverList.get(); Boolean deviceToDriver;
        for(SummaryReportResponse summaryReportOne : summaryReports) {
             Long driverId;String driverName = null; String fuel = null;
            try {
                List<Device> devices= deviceList.stream().filter(device1 -> device1.getId().equals(summaryReportOne.getDeviceId())).toList();

                if (!devices.isEmpty()){
                    driverId = devices.get(0).getDriverId();
                    if (driverId != null) {
                        driverName = driverList.stream().filter(driver -> driver.getId().equals(driverId)).findFirst().get().getName();
                    }
                    fuel = devices.get(0).getFuel();
                }
                summaryReportOne.setDriverName(driverName);

            }catch (Exception | Error ignored){

            }

            if (summaryReportOne.getDistance() != null && !Objects.equals(summaryReportOne.getDistance(), "")) {
                totalDistance = Math.abs(Double.parseDouble(summaryReportOne.getDistance()) / 1000);
                roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
                summaryReportOne.setDistance(Double.toString(roundOffDistance));
            }
            if(fuel != null) {
                if(fuel != null && !Objects.equals(fuel, "") && fuel.startsWith("{")) {
                    JSONObject obj = new JSONObject(fuel);
                    if(obj.has("fuelPerKM")) {
                        litres=obj.getDouble("fuelPerKM");

                    }
                }
            }

            distance = Double.parseDouble(summaryReportOne.getDistance().toString());
            if(distance > 0) {
                Fuel = (distance*litres)/100;
            }

            roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
            summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));

            if(summaryReportOne.getEngineHours() != null && !Objects.equals(summaryReportOne.getEngineHours(), "")) {

                summaryReportOne.setEngineHours(utilities.durationCalculation(summaryReportOne.getEngineHours()));
            }

            if(summaryReportOne.getAverageSpeed() != null && !Objects.equals(summaryReportOne.getAverageSpeed(), "")) {
                summaryReportOne.setAverageSpeed(String.valueOf(
                        utilities.speedConverter(Double.parseDouble(String.valueOf(roundOffDistance)))
                ));
            }

            if(summaryReportOne.getMaxSpeed() != null && !Objects.equals(summaryReportOne.getMaxSpeed(), "")) {
                summaryReportOne.setMaxSpeed(String.valueOf(
                        utilities.speedConverter(Double.parseDouble(summaryReportOne.getMaxSpeed()))
                ));
            }
        }
        return summaryReports;
    }
//
//    public List<DeviceTempHum>  deviceTempAndHumProcessHandler(List<MongoPositions> mongoPositions,String timeOffset){
//        List<DeviceTempHum> positions = new ArrayList<>();
//        ObjectMapper oMapper = new ObjectMapper();
//
//        for(MongoPositions mongoPosition: mongoPositions){
//            DeviceTempHum deviceTempHum = DeviceTempHum.builder()
//                    .id(mongoPosition.get_id().toString())
//                    .deviceId(mongoPosition.getDeviceid())
//                    .deviceName(mongoPosition.getDeviceName())
//                    .driverId(mongoPosition.getDriverid())
//                    .driverName(mongoPosition.getDriverName())
//                    .speed(utilities.speedConverter(mongoPosition.getSpeed()))
//                    .deviceTime(utilities.timeZoneConverter(mongoPosition.getDevicetime(), timeOffset))
//                    .temperature(utilities.temperatureCalculations(oMapper.convertValue(mongoPosition.getAttributes(), Map.class)))
//                    .humidity(utilities.humidityCalculations(oMapper.convertValue(mongoPosition.getAttributes(), Map.class)))
//                    .latitude(mongoPosition.getLatitude())
//                    .longitude(mongoPosition.getLongitude())
//                    .attributes(mongoPosition.getAttributes().toString())
//                    .address(mongoPosition.getAddress())
//                    .build();
//
//
//            positions.add(deviceTempHum);
//        }
//        return positions;
//    }
//
    public List<EventReportResponse> eventsReportProcessHandler(List<MongoEvents> mongoEventsList, String timeOffset){
        List<EventReportResponse> eventReportList = new ArrayList<>();
        List<MongoPositions> position;
        List<String> positionIds;
        positionIds = mongoEventsList.stream().
                map(MongoEvents::getPositionid)
                .collect(Collectors.toList());
        Optional<List<MongoPositions>> mongoPositionsList = mongoPositionsRepository.findAllBy_idIn(positionIds);
        position = mongoPositionsList.get();
        for(MongoEvents mongoEvent: mongoEventsList){
            Double longitude = null; Double latitude = null;
            try {
                List<MongoPositions> result = position.stream()
                        .filter(result1 -> result1.get_id().equals(mongoEvent.getPositionid())).toList();
                if (result.isEmpty()){
                    longitude = result.get(0).getLongitude();
                    latitude = result.get(0).getLatitude();
                }
            }catch (Exception ignored){

            }
//            if(mongoEvent.getPositionid() != null){
//                position = mongoPositionsRepository.findById(mongoEvent.getPositionid());
//            }
            EventReportResponse eventReport = EventReportResponse.builder()
                    .deviceId(mongoEvent.getDeviceid())
                    .deviceName(mongoEvent.getDeviceName())
                    .driverId(mongoEvent.getDriverid())
                    .driverName(mongoEvent.getDriverName())
                    .eventType(mongoEvent.getType())
                    .geofenceId(mongoEvent.getGeofenceid())
                    .attributes(mongoEvent.getAttributes())
                    .positionId(mongoEvent.getPositionid())
                    .serverTime(utilities.timeZoneConverter(mongoEvent.getServertime(), timeOffset))
                    .eventId(mongoEvent.get_id().toString())
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();

            eventReportList.add(eventReport);
        }
        return eventReportList;
    }

}
