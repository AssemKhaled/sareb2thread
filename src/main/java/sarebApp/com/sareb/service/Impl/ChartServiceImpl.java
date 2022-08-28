package sarebApp.com.sareb.service.Impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.ApiResponseBuilder;
import sarebApp.com.sareb.dto.responses.DistanceFuelEngineResponse;
import sarebApp.com.sareb.dto.responses.MergeHoursIgnitionResponse;
import sarebApp.com.sareb.dto.responses.SummaryReportResponse;
import sarebApp.com.sareb.entities.Device;
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.helper.Impl.AssistantServiceImpl;
import sarebApp.com.sareb.helper.Impl.ReportsHelper;
import sarebApp.com.sareb.repository.DeviceRepository;
import sarebApp.com.sareb.repository.MongoPositionsRepo;
import sarebApp.com.sareb.repository.UserClientDeviceRepository;
import sarebApp.com.sareb.repository.UserRepository;
import sarebApp.com.sareb.service.ChartService;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {
    private final UserRepository userRepository;
    private final MongoPositionsRepo mongoPositionsRepo;
    private static final Log logger = LogFactory.getLog(ChartServiceImpl.class);
    private final ReportsHelper reportsHelper;

    private final UserClientDeviceRepository userClientDeviceRepository;

    private final DeviceRepository deviceRepository;
    private final AssistantServiceImpl assistantServiceImpl;
    @Value("${summaryUrl}")
    private String summaryUrl;


    @Override
    public ApiResponse<List<DistanceFuelEngineResponse>> getDistanceFuelEngine(Long userId) {
        ApiResponseBuilder<List<DistanceFuelEngineResponse>>builder=new ApiResponseBuilder<>();
        List<DistanceFuelEngineResponse>results=new ArrayList<>();
        List<DistanceFuelEngineResponse>data=new ArrayList<>();List<SummaryReportResponse>summaryReports;
        User loggeduser;Device deviceinfo;String GET_URL = summaryUrl;List<Long>alldevices;
        if(userId!=0){
            Optional<User> user=userRepository.findById(userId);
            if(user.isPresent()){
                loggeduser=user.get();
                if (loggeduser.getAccountType().equals(4)){
                    alldevices=userClientDeviceRepository.getDevicesIds(userId);
                    if(alldevices.size()>0){
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        inputFormat.setLenient(false);
                        outputFormat.setLenient(false);
                        Date dateTo;
                        Date dateFrom = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateFrom);
                        c.add(Calendar.DATE, 1);
                        dateTo = c.getTime();
                        String from = "";
                        String to = "";
                        from = outputFormat.format(dateFrom);
                        to = outputFormat.format(dateTo);
                        summaryReports= (List<SummaryReportResponse>) reportsHelper.returnFromTraccar(GET_URL,"summary",alldevices,from,to,"allEvents",1,0,25).getBody();
                        for (SummaryReportResponse summaryReportone:summaryReports){
                            Double totalDistance = 0.0 ;
                            double roundOffDistance = 0.0;
                            Double litres=10.0;
                            double roundOffFuel=0.0;
                            Double Fuel =0.0;
                            Double distance=0.0;
                            if(summaryReportone.getDistance() != null && summaryReportone.getDistance() != "") {
                                totalDistance = Math.abs(  Double.parseDouble(summaryReportone.getDistance())/1000  );
                                roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
                                summaryReportone.setDistance(Double.toString(roundOffDistance));


                            }
                            Optional<Device> device=deviceRepository.findById(summaryReportone.getDeviceId());
                            if(device.isPresent()){
                                deviceinfo=device.get();
                                if(deviceinfo.getFuel() != null) {
                                    if(deviceinfo.getFuel() != null && deviceinfo.getFuel() != "" && deviceinfo.getFuel().startsWith("{")) {
                                        JSONObject obj = new JSONObject(deviceinfo.getFuel());
                                        if(obj.has("fuelPerKM")) {
                                            litres=obj.getDouble("fuelPerKM");

                                        }
                                    }
                                }
                            }else {
                                builder.setMessage("Device is Not Found");
                                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                                builder.setSize(0);
                                builder.setSuccess(false);
                                return builder.build();
                            }
                            distance = Double.parseDouble(summaryReportone.getDistance().toString());
                            if(distance > 0) {
                                Fuel = (distance*litres)/100;
                            }

                            roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
                            summaryReportone.setSpentFuel(Double.toString(roundOffFuel));
                            data.add(
                                    DistanceFuelEngineResponse.builder().spentFuel(summaryReportone.getSpentFuel()).distance(summaryReportone.getDistance()).deviceId(summaryReportone.getDeviceId()).deviceName(summaryReportone.getDeviceName()).build()
                            );
                        if (data.size()==10){

                            Double newData = Double.parseDouble( data.get(0).getDistance().toString() );
                            for (int i=0;i<data.size();i++){
                                Double oldData = Double.parseDouble( data.get(i).getDistance().toString() );
                                if(newData > oldData) {

                                    results.get(i).setSpentFuel(data.get(0).getSpentFuel());
                                    results.get(i).setDistance(data.get(0).getDistance());
                                    results.get(i).setDeviceId(data.get(0).getDeviceId());
                                    results.get(i).setDeviceName(data.get(0).getDeviceName());
                                    break;

                                }
                            }
                        }
                        if (results.size()<10){

                            results = data;
                        }

                        }


                    }
                    else {
                        builder.setMessage("success");
                        builder.setStatusCode(HttpStatus.OK.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(true);
                        return builder.build();
                    }

                }
                else if (loggeduser.getAccountType().equals(3)) {
                   alldevices =deviceRepository.getDevicesbyclientids(userId);
                    if(alldevices.size()>0){
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        inputFormat.setLenient(false);
                        outputFormat.setLenient(false);
                        Date dateTo;
                        Date dateFrom = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateFrom);
                        c.add(Calendar.DATE, 1);
                        dateTo = c.getTime();
                        String from = "";
                        String to = "";
                        from = outputFormat.format(dateFrom);
                        to = outputFormat.format(dateTo);
                        summaryReports= (List<SummaryReportResponse>) reportsHelper.returnFromTraccar(GET_URL,"summary",alldevices,from,to,"allEvents",1,0,25).getBody();
                        for (SummaryReportResponse summaryReportone:summaryReports){
                            Double totalDistance = 0.0 ;
                            double roundOffDistance = 0.0;
                            Double litres=10.0;
                            double roundOffFuel=0.0;
                            Double Fuel =0.0;
                            Double distance=0.0;
                            if(summaryReportone.getDistance() != null && summaryReportone.getDistance() != "") {
                                totalDistance = Math.abs(  Double.parseDouble(summaryReportone.getDistance())/1000  );
                                roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
                                summaryReportone.setDistance(Double.toString(roundOffDistance));


                            }
                            Optional<Device> device=deviceRepository.findById(summaryReportone.getDeviceId());
                            if(device.isPresent()){
                                deviceinfo=device.get();
                                if(deviceinfo.getFuel() != null) {
                                    if(deviceinfo.getFuel() != null && deviceinfo.getFuel() != "" && deviceinfo.getFuel().startsWith("{")) {
                                        JSONObject obj = new JSONObject(deviceinfo.getFuel());
                                        if(obj.has("fuelPerKM")) {
                                            litres=obj.getDouble("fuelPerKM");

                                        }
                                    }
                                }
                            }else {
                                builder.setMessage("Device is Not Found");
                                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                                builder.setSize(0);
                                builder.setSuccess(false);
                                return builder.build();
                            }
                            distance = Double.parseDouble(summaryReportone.getDistance().toString());
                            if(distance > 0) {
                                Fuel = (distance*litres)/100;
                            }

                            roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
                            summaryReportone.setSpentFuel(Double.toString(roundOffFuel));
                            data.add(
                                    DistanceFuelEngineResponse.builder().spentFuel(summaryReportone.getSpentFuel()).distance(summaryReportone.getDistance()).deviceId(summaryReportone.getDeviceId()).deviceName(summaryReportone.getDeviceName()).build()
                            );
                            if (data.size()==10){

                                Double newData = Double.parseDouble( data.get(0).getDistance().toString() );
                                for (int i=0;i<data.size();i++){
                                    Double oldData = Double.parseDouble( data.get(i).getDistance().toString() );
                                    if(newData > oldData) {

                                        results.get(i).setSpentFuel(data.get(0).getSpentFuel());
                                        results.get(i).setDistance(data.get(0).getDistance());
                                        results.get(i).setDeviceId(data.get(0).getDeviceId());
                                        results.get(i).setDeviceName(data.get(0).getDeviceName());
                                        break;

                                    }
                                }
                            }
                            if (results.size()<10){

                                results = data;
                            }

                        }


                    }
                        else {
                        builder.setMessage("success");
                        builder.setStatusCode(HttpStatus.OK.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(true);
                        return builder.build();
                    }
                }
                else if (loggeduser.getAccountType().equals(2)) {
                    List<Long> usersIds=assistantServiceImpl.getUserChildrens(userId);
                    List<Integer> userDeviceIds=deviceRepository.deviceIdsByUserChildrens(usersIds);
                    List<Long>ids=userDeviceIds.stream()
                            .map(Integer::longValue)
                            .collect(Collectors.toList());
                    alldevices=deviceRepository.getDevicesByIDs(ids);
                    if(alldevices.size()>0){
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        inputFormat.setLenient(false);
                        outputFormat.setLenient(false);
                        Date dateTo;
                        Date dateFrom = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateFrom);
                        c.add(Calendar.DATE, 1);
                        dateTo = c.getTime();
                        String from = "";
                        String to = "";
                        from = outputFormat.format(dateFrom);
                        to = outputFormat.format(dateTo);
                        summaryReports= (List<SummaryReportResponse>) reportsHelper.returnFromTraccar(GET_URL,"summary",alldevices,from,to,"allEvents",1,0,25).getBody();
                        for (SummaryReportResponse summaryReportone:summaryReports){
                            Double totalDistance = 0.0 ;
                            double roundOffDistance = 0.0;
                            Double litres=10.0;
                            double roundOffFuel=0.0;
                            Double Fuel =0.0;
                            Double distance=0.0;
                            if(summaryReportone.getDistance() != null && summaryReportone.getDistance() != "") {
                                totalDistance = Math.abs(  Double.parseDouble(summaryReportone.getDistance())/1000  );
                                roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
                                summaryReportone.setDistance(Double.toString(roundOffDistance));


                            }
                            Optional<Device> device=deviceRepository.findById(summaryReportone.getDeviceId());
                            if(device.isPresent()){
                                deviceinfo=device.get();
                                if(deviceinfo.getFuel() != null) {
                                    if(deviceinfo.getFuel() != null && deviceinfo.getFuel() != "" && deviceinfo.getFuel().startsWith("{")) {
                                        JSONObject obj = new JSONObject(deviceinfo.getFuel());
                                        if(obj.has("fuelPerKM")) {
                                            litres=obj.getDouble("fuelPerKM");

                                        }
                                    }
                                }
                            }else {
                                builder.setMessage("Device is Not Found");
                                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                                builder.setSize(0);
                                builder.setSuccess(false);
                                return builder.build();
                            }
                            distance = Double.parseDouble(summaryReportone.getDistance().toString());
                            if(distance > 0) {
                                Fuel = (distance*litres)/100;
                            }

                            roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
                            summaryReportone.setSpentFuel(Double.toString(roundOffFuel));
                            data.add(
                                    DistanceFuelEngineResponse.builder().spentFuel(summaryReportone.getSpentFuel()).distance(summaryReportone.getDistance()).deviceId(summaryReportone.getDeviceId()).deviceName(summaryReportone.getDeviceName()).build()
                            );
                            if (data.size()==10){

                                Double newData = Double.parseDouble( data.get(0).getDistance().toString() );
                                for (int i=0;i<data.size();i++){
                                    Double oldData = Double.parseDouble( data.get(i).getDistance().toString() );
                                    if(newData > oldData) {

                                        results.get(i).setSpentFuel(data.get(0).getSpentFuel());
                                        results.get(i).setDistance(data.get(0).getDistance());
                                        results.get(i).setDeviceId(data.get(0).getDeviceId());
                                        results.get(i).setDeviceName(data.get(0).getDeviceName());
                                        break;

                                    }
                                }
                            }
                            if (results.size()<10){

                                results = data;
                            }

                        }


                    }
                    else {
                        builder.setMessage("success");
                        builder.setStatusCode(HttpStatus.OK.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(true);
                        return builder.build();
                    }
                }
                else {
                    List<Integer>devices=deviceRepository.getAllDeviceIds();
                    alldevices=devices.stream()
                            .map(Integer::longValue)
                            .collect(Collectors.toList());
                    if(alldevices.size()>0){
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        inputFormat.setLenient(false);
                        outputFormat.setLenient(false);
                        Date dateTo;
                        Date dateFrom = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateFrom);
                        c.add(Calendar.DATE, 1);
                        dateTo = c.getTime();
                        String from = "";
                        String to = "";
                        from = outputFormat.format(dateFrom);
                        to = outputFormat.format(dateTo);
                        summaryReports= (List<SummaryReportResponse>) reportsHelper.returnFromTraccar(GET_URL,"summary",alldevices,from,to,"allEvents",1,0,25).getBody();
                        for (SummaryReportResponse summaryReportone:summaryReports){
                            Double totalDistance = 0.0 ;
                            double roundOffDistance = 0.0;
                            Double litres=10.0;
                            double roundOffFuel=0.0;
                            Double Fuel =0.0;
                            Double distance=0.0;
                            if(summaryReportone.getDistance() != null && summaryReportone.getDistance() != "") {
                                totalDistance = Math.abs(  Double.parseDouble(summaryReportone.getDistance())/1000  );
                                roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
                                summaryReportone.setDistance(Double.toString(roundOffDistance));


                            }
                            Optional<Device> device=deviceRepository.findById(summaryReportone.getDeviceId());
                            if(device.isPresent()){
                                deviceinfo=device.get();
                                if(deviceinfo.getFuel() != null) {
                                    if(deviceinfo.getFuel() != null && deviceinfo.getFuel() != "" && deviceinfo.getFuel().startsWith("{")) {
                                        JSONObject obj = new JSONObject(deviceinfo.getFuel());
                                        if(obj.has("fuelPerKM")) {
                                            litres=obj.getDouble("fuelPerKM");

                                        }
                                    }
                                }
                            }else {
                                builder.setMessage("Device is Not Found");
                                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                                builder.setSize(0);
                                builder.setSuccess(false);
                                return builder.build();
                            }
                            distance = Double.parseDouble(summaryReportone.getDistance().toString());
                            if(distance > 0) {
                                Fuel = (distance*litres)/100;
                            }

                            roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
                            summaryReportone.setSpentFuel(Double.toString(roundOffFuel));
                            data.add(
                                    DistanceFuelEngineResponse.builder().spentFuel(summaryReportone.getSpentFuel()).distance(summaryReportone.getDistance()).deviceId(summaryReportone.getDeviceId()).deviceName(summaryReportone.getDeviceName()).build()
                            );
                            if (data.size()==10){

                                Double newData = Double.parseDouble( data.get(0).getDistance().toString() );
                                for (int i=0;i<data.size();i++){
                                    Double oldData = Double.parseDouble( data.get(i).getDistance().toString() );
                                    if(newData > oldData) {

                                        results.get(i).setSpentFuel(data.get(0).getSpentFuel());
                                        results.get(i).setDistance(data.get(0).getDistance());
                                        results.get(i).setDeviceId(data.get(0).getDeviceId());
                                        results.get(i).setDeviceName(data.get(0).getDeviceName());
                                        break;

                                    }
                                }
                            }
                            if (results.size()<10){

                                results = data;
                            }

                        }


                    }
                    else {
                        builder.setMessage("success");
                        builder.setStatusCode(HttpStatus.OK.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(true);
                        return builder.build();
                    }

                }
            }else {
                builder.setMessage("Logged user is not found");
                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                builder.setEntity(null);
                builder.setSize(0);
                builder.setSuccess(false);
                return builder.build();
            }
        }else {
            builder.setMessage("User ID is required");
            builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
            builder.setEntity(null);
            builder.setSize(0);
            builder.setSuccess(false);
            return builder.build();
        }
        builder.setMessage("success");
        builder.setStatusCode(HttpStatus.OK.value());
        builder.setEntity(results);
        builder.setSize(results.size());
        builder.setSuccess(true);
        return builder.build();
    }

    @Override
    public ApiResponse<List<MergeHoursIgnitionResponse>> getMergeHoursIgnition(Long userId) {
        ApiResponseBuilder<List<MergeHoursIgnitionResponse>>builder=new ApiResponseBuilder<>();
        List<MergeHoursIgnitionResponse>results=new ArrayList<>();
        User loggedUser=null;
        List<Map>data=new ArrayList<>();
        List<Long>alldevices=new ArrayList<>();
        List<String>positions=new ArrayList<>();
        Integer ignitionON= 0;
        Integer ignitionOFF= 0;
        Map dev=new HashMap<>();
        if (userId!=0){
            Optional<User> user=userRepository.findById(userId);
            if (user.isPresent()){
                loggedUser=user.get();
                if (loggedUser.getAccountType().equals(4)){
                    alldevices=userClientDeviceRepository.getDevicesIds(userId);
                    if (alldevices.size()>0){
                        positions=deviceRepository.getAllPositionsObjectIdsByIds(alldevices);
                        ignitionON=mongoPositionsRepo.getCountFromAttrbuitesChart(positions,"ignition",true);
                        ignitionOFF=mongoPositionsRepo.getCountFromAttrbuitesChart(positions,"ignition",false);
                        data=mongoPositionsRepo.getCharts(positions);
                        dev = new HashMap();
                        dev.put("ignition_on", ignitionON);
                        dev.put("ignition_off" ,ignitionOFF);

                    }else {
                        builder.setMessage("success");
                        builder.setStatusCode(HttpStatus.OK.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(true);
                        return builder.build();
                    }
                }
                else if (loggedUser.getAccountType().equals(3)) {
                    alldevices =deviceRepository.getDevicesbyclientids(userId);
                    if (alldevices.size()>0){
                        positions=deviceRepository.getAllPositionsObjectIdsByIds(alldevices);
                        ignitionON=mongoPositionsRepo.getCountFromAttrbuitesChart(positions,"ignition",true);
                        ignitionOFF=mongoPositionsRepo.getCountFromAttrbuitesChart(positions,"ignition",false);
                        data=mongoPositionsRepo.getCharts(positions);
                        dev = new HashMap();
                        dev.put("ignition_on", ignitionON);
                        dev.put("ignition_off" ,ignitionOFF);
                    }else {
                        builder.setMessage("success");
                        builder.setStatusCode(HttpStatus.OK.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(true);
                        return builder.build();
                    }

                }
                else if (loggedUser.getAccountType().equals(2)) {
                    List<Long> usersIds=assistantServiceImpl.getUserChildrens(userId);
                    List<Integer> userDeviceIds=deviceRepository.deviceIdsByUserChildrens(usersIds);
                    List<Long>ids=userDeviceIds.stream()
                            .map(Integer::longValue)
                            .collect(Collectors.toList());
                    alldevices=deviceRepository.getDevicesByIDs(ids);
                    if (alldevices.size()>0){
                        positions=deviceRepository.getAllPositionsObjectIdsByIds(alldevices);
                        ignitionON=mongoPositionsRepo.getCountFromAttrbuitesChart(positions,"ignition",true);
                        ignitionOFF=mongoPositionsRepo.getCountFromAttrbuitesChart(positions,"ignition",false);
                        data=mongoPositionsRepo.getCharts(positions);
                        dev = new HashMap();
                        dev.put("ignition_on", ignitionON);
                        dev.put("ignition_off" ,ignitionOFF);
                    }else {
                        builder.setMessage("success");
                        builder.setStatusCode(HttpStatus.OK.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(true);
                        return builder.build();
                    }
                }
                else {
                    List<Integer>devices=deviceRepository.getAllDeviceIds();
                    alldevices=devices.stream()
                            .map(Integer::longValue)
                            .collect(Collectors.toList());
                    if(alldevices.size()>0) {
                        positions=deviceRepository.getAllPositionsObjectIdsByIds(alldevices);
                        ignitionON=mongoPositionsRepo.getCountFromAttrbuitesChart(positions,"ignition",true);
                        ignitionOFF=mongoPositionsRepo.getCountFromAttrbuitesChart(positions,"ignition",false);
                        data=mongoPositionsRepo.getCharts(positions);
                        dev = new HashMap();
                        dev.put("ignition_on", ignitionON);
                        dev.put("ignition_off" ,ignitionOFF);
                    }else {
                        builder.setMessage("success");
                        builder.setStatusCode(HttpStatus.OK.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(true);
                        return builder.build();
                    }
                }
                results.add(
                        MergeHoursIgnitionResponse.builder().hours(
                                data
                        ).status(dev).build()
                );

            }
            else {
                builder.setMessage("Logged user is not found");
                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                builder.setEntity(null);
                builder.setSize(0);
                builder.setSuccess(false);
                return builder.build();
            }
        }else {
            builder.setMessage("User ID is required");
            builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
            builder.setEntity(null);
            builder.setSize(0);
            builder.setSuccess(false);
            return builder.build();
        }
        builder.setMessage("success");
        builder.setStatusCode(HttpStatus.OK.value());
        builder.setEntity(results);
        builder.setSize(results.size());
        builder.setSuccess(true);
        return builder.build();
    }
}
