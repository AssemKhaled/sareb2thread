package sarebApp.com.sareb.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.ApiResponseBuilder;
import sarebApp.com.sareb.dto.responses.AllDeviceLiveDataResponse;
import sarebApp.com.sareb.entities.Device;
import sarebApp.com.sareb.entities.MongoPositions;
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.exception.ApiGetException;
import sarebApp.com.sareb.helper.Impl.AssistantServiceImpl;
import sarebApp.com.sareb.repository.DeviceRepository;
import sarebApp.com.sareb.repository.MongoPositionsRepository;
import sarebApp.com.sareb.repository.UserRepository;
import sarebApp.com.sareb.service.DeviceService;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Assem
 */
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final AssistantServiceImpl assistantServiceImpl;
    private final MongoPositionsRepository mongoPositionsRepository;


    @Override
    public ApiResponse<List<AllDeviceLiveDataResponse>> getAllDeviceDashBoard(Long userId, int offset, String search) {
        ApiResponseBuilder<List<AllDeviceLiveDataResponse>> builder = new ApiResponseBuilder<>();
        List<AllDeviceLiveDataResponse> result = new ArrayList<>();
        List<Long> usersIds;
        List<String> lastDataIds;
        List<Integer> userDeviceIds;
        User loggedUser ;
        int sizes=0;

        if(userId.equals(0)) {
            builder.setMessage("User ID is required");
            builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
            builder.setEntity(null);
            builder.setSize(0);
            builder.setSuccess(false);
            return builder.build();
        }
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            loggedUser = optionalUser.get();
        }else {
            builder.setMessage("Logged user is not found");
            builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
            builder.setEntity(null);
            builder.setSize(0);
            builder.setSuccess(false);
            return builder.build();
        }
        Optional<List<Device>> optionalDeviceList = Optional.empty();
        Optional<List<Device>> optionalDevices = Optional.empty();
        List<Integer> deviceIds;

        List<Long> ids;
        if (loggedUser.getAccountType().equals(1)){

            userDeviceIds=deviceRepository.getAllDeviceIds();
            ids = userDeviceIds.stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            if(search.equals("")){
                offset=offset/10;
                optionalDevices = deviceRepository.findAllByIdIn(ids , PageRequest.of(offset,10));
                sizes=userDeviceIds.size();
            }
            else {
                optionalDevices = deviceRepository.getAllDevicesBySearch(search,offset,10);
                sizes= optionalDevices.stream().toList().size();
            }
        }
        else if (loggedUser.getAccountType().equals(2)){
            usersIds = assistantServiceImpl.getUserChildrens(userId);
            userDeviceIds = deviceRepository.deviceIdsByUserChildrens(usersIds);
            ids = userDeviceIds.stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());

            if(search.equals("")){
                offset=offset/10;
                optionalDevices = deviceRepository.findAllByIdInAndDeleteDate(ids,null,PageRequest.of(offset,10));
                sizes=userDeviceIds.size();
            }
            else {
                optionalDevices = deviceRepository.getDevicesByDevicesIdsvendor(ids, search, offset);
                sizes = optionalDevices.stream().toList().size();
            }
        }
        else if (loggedUser.getAccountType().equals(3)){
            Date date=new Date();
            if(search.equals("")){
                optionalDevices = deviceRepository.getAllDevicesAndUser_idANDAndEndDate(offset, 10,userId);
                sizes = optionalDevices.stream().toList().size();
            }
            else {
                optionalDevices = deviceRepository.getAllDevicesBySearchAndUser_idANDAndEndDate(search,offset,10,userId);
                sizes = optionalDevices.stream().toList().size();

            }
        } else if (loggedUser.getAccountType().equals(4)) {

            deviceIds = deviceRepository.newGetDevicesIds(userId);
            ids = deviceIds.stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            Date date=new Date();

            if(search.equals("")){
                offset=offset/10;
                optionalDevices = deviceRepository.findAllByIdInAndEndDateGreaterThan(ids,date,PageRequest.of(offset,10));
                Optional<List<Device>> s=deviceRepository.findAllByIdInAndEndDateGreaterThan(ids,date);
                sizes = s.map(List::size).orElse(0);
            }
            else {
                optionalDevices = deviceRepository.getDevicesByDevicesIdsvendor(ids,search,offset);
                sizes = optionalDevices.stream().toList().size();
            }
        }
        List<Device> deviceList=new ArrayList<>();
        if (optionalDevices.isPresent()) {
            deviceList = optionalDevices.get();
            lastDataIds = deviceList.stream()
                    .map(Device::getPositionid)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Optional<List<MongoPositions>> mongoPositionsList = mongoPositionsRepository.findAllBy_idIn(lastDataIds);
            List<MongoPositions> mongoPositions = mongoPositionsList.get();

            for (Device device:deviceList) {
                double roundTemp; double roundHum;
                String status;
                Boolean valid = null; String strDate = null; Object attributes = null;
                Double speed = null; Double longitude = null; Double latitude = null;
                Double power = null; Double operator = null; Boolean ignition = null;
                Boolean expired; Long leftDays = null; String vehicleStatus;
                Date now = new Date();

                roundTemp = Math.round(device.getLastTemp() * 100.0) / 100.0;
                roundHum = Math.round(device.getLastHum() * 100.0) / 100.0;
                if (device.getExpired() == 1 && device.getExpired() !=null) {
                    expired = true;
                } else if (device.getExpired()==null) {
                    expired=false;
                } else {
                    expired = false;
                }

                if (device.getEnd_date() !=null){
                    leftDays = ChronoUnit.DAYS.between(now.toInstant(),device.getEnd_date().toInstant());
                }
                vehicleStatus = assistantServiceImpl.getVehicleStatus(device.getLastupdate());
                try {
                    List<MongoPositions> mongoPositionsList1 = mongoPositions.stream()
                            .filter(mongoPositions1 -> mongoPositions1.getDeviceid().equals(device.getId()))
                            .collect(Collectors.toList());
                    if (!mongoPositionsList1.isEmpty()){
                        attributes = mongoPositionsList1.get(0).getAttributes();
                        speed = mongoPositionsList1.get(0).getSpeed()* (1.852);
                        longitude = mongoPositionsList1.get(0).getLongitude();
                        latitude = mongoPositionsList1.get(0).getLatitude();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        strDate = formatter.format(mongoPositionsList1.get(0).getServertime());
                        ObjectMapper mapper = new ObjectMapper();
                        String json = null;
                        try {
                            json = mapper.writeValueAsString(attributes);
                        } catch (JsonProcessingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        status = assistantServiceImpl.getSarebVehicleStatus(device.getLastupdate(),mongoPositionsList1.get(0).get_id(),json,mongoPositionsList1.get(0).getSpeed());
                        JSONObject obj = new JSONObject(json);
                        if (obj.has("operator")){
                            operator = obj.getDouble("operator");
                        }
                        if (obj.has("power")){
                            power = obj.getDouble("power");								}
                        if (obj.has("ignition")){
                            ignition = obj.getBoolean("ignition");
                        }

                        if(mongoPositionsList1.get(0).getValid()) {
                            valid = true;
                        }
                        else {
                            valid = false;
                        }
                    }else {
                        status = "No data";
                    }

                }catch (Exception e){
                    throw new ApiGetException(e.getLocalizedMessage());
                }
                result.add(
                        AllDeviceLiveDataResponse
                                .builder()
                                .id(device.getId())
                                .uniqueId(device.getUniqueid())
                                .deviceName(device.getName())
                                .vehicleStatus(vehicleStatus)
                                .status(status)
                                .lastUpdate(strDate)
                                .valid(valid)
                                .attributes(attributes)
                                .speed(speed)
                                .latitude(latitude)
                                .longitude(longitude)
                                .power(power)
                                .operator(operator)
                                .ignition(ignition)
                                .temperature(roundTemp)
                                .humidity(roundHum)
                                .create_date(device.getCreate_date())
                                .expired(expired)
                                .positionId(device.getPosition_id())
                                .leftLetter(device.getLeft_letter())
                                .rightLetter(device.getRight_letter())
                                .middleLetter(device.getMiddle_letter())
                                .leftDays(leftDays)
                                .sequence_number(device.getSequence_number())
                                .photo(device.getPhoto())
                                .build()
                );
            }

        }

        builder.setMessage("success");
        builder.setStatusCode(HttpStatus.OK.value());
        builder.setEntity(result);
        builder.setSize(sizes);
        builder.setSuccess(true);
        return builder.build();
    }


 }


