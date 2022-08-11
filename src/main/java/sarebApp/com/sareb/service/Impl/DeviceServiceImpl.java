package sarebApp.com.sareb.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.ApiResponseBuilder;
import sarebApp.com.sareb.dto.responses.AllDeviceLiveDataResponse;
import sarebApp.com.sareb.dto.responses.MongoMapperResponse;
import sarebApp.com.sareb.dto.responses.VehicleDetailsResponse;
import sarebApp.com.sareb.entities.*;
import sarebApp.com.sareb.exception.ApiGetException;
import sarebApp.com.sareb.helper.Impl.AssistantServiceImpl;
import sarebApp.com.sareb.helper.Impl.Utilities;
import sarebApp.com.sareb.repository.*;
import sarebApp.com.sareb.service.DeviceService;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
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
    private final UserClientDeviceReposiitory userClientDeviceReposiitory;
    private final DriverRepository driverRepository;
    private final Utilities utilities;

    @Override
    public ApiResponse<?> createDevice(Device device, Long userId) {
        return null;
    }

    @Override
    public ApiResponse<List<AllDeviceLiveDataResponse>> getAllDevicesDashBoard(Long userId, int offset, String search) {
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

                if (device.getEndDate() !=null){
                    leftDays = ChronoUnit.DAYS.between(now.toInstant(),device.getEndDate().toInstant());
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

    @Override
    public ApiResponse<VehicleDetailsResponse> getDeviceDetails(Long userId, Long deviceId) {

        ApiResponseBuilder<VehicleDetailsResponse> builder = new ApiResponseBuilder<>();
        Optional<UserClientDevice> optionalUserClientDevice = userClientDeviceReposiitory.findByUseridAndDeviceid(userId,deviceId);
        Map<Object, Object> sensorList = new HashMap<>();
        ArrayList<Map<Object,Object>> lastPoints = new ArrayList<>();
        MongoMapperResponse mongoMapper ;
        VehicleDetailsResponse result ;
        Double latitude = null;Double longitude = null;Double speed = null;String address = null;Long id;
        Object attributes = null; Long driverId; String driverPhoto = null; String driverUniqueId = null;
        String driverName = null; String driverNum = null;
        Optional<User> user = userRepository.findByIdAndDeleteDate(userId,null);
        if (user.isEmpty()) {
            throw new ApiGetException("User Is Not Found");
        }
        Optional<Device> optionalDevice ;
        if (user.get().getAccountType().equals(1) || user.get().getAccountType().equals(2)){
            optionalDevice = deviceRepository.findByIdAndDeleteDate(deviceId,null);
        } else if (user.get().getAccountType().equals(3)) {
            optionalDevice = deviceRepository.findByUser_idAndDeleteDate(userId,null);
        } else {
            if (optionalUserClientDevice.isPresent()) {
                UserClientDevice userDevice = optionalUserClientDevice.get();
                optionalDevice = deviceRepository.findByIdAndDeleteDate(userDevice.getDeviceid(),null);
            } else {
                builder.setStatusCode(200);
                builder.setEntity(null);
                builder.setMessage("Can`t Find Any Vehicles For This User");
                builder.setSize(0);
                builder.setSuccess(true);
                return builder.build();
            }
        }

            if (optionalDevice.isPresent()){
                Device device = optionalDevice.get();
                driverId = driverRepository.deviceDriverId(device.getId());
                if (driverId != null) {
                    Optional<Driver> optionalDriver = driverRepository.findByIdAndDeleteDate(driverId,null);
                    if (optionalDriver.isPresent()){
                        Driver driver = optionalDriver.get();
                        driverPhoto = driver.getPhoto();
                        driverUniqueId = driver.getUniqueid();
                        driverName = driver.getName();
                        driverNum = driver.getMobile_num();
                    }
                }
                if (device.getPositionid()!=null){
                    Optional<MongoPositions> optionalMongoPositions = mongoPositionsRepository.findBy_id(device.getPositionid());
                    if (optionalMongoPositions.isPresent()){

                        MongoPositions mongoPositions = optionalMongoPositions.get();
                        latitude = mongoPositions.getLatitude();
                        longitude = mongoPositions.getLongitude();
                        address = mongoPositions.getAddress();
                        speed = mongoPositions.getSpeed()*(1.852);
                        mongoMapper = utilities.MongoObjJsonMapper(mongoPositions.getAttributes(),speed,device.getId(),device.getLastupdate());
                        attributes = mongoMapper.getAttributes();
                        lastPoints = mongoMapper.getLastPoints();

                    }
                }

                if(device.getSensorSettings() != null) {
                    JSONObject obj2 = new JSONObject(device.getSensorSettings());
                    Iterator<String> keys = obj2.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        sensorList.put(key , obj2.get(key).toString());
                    }
                }

                result = VehicleDetailsResponse
                        .builder()
                        .id(device.getId())
                        .deviceName(device.getName())
                        .uniqueId(device.getUniqueid())
                        .sequenceNumber(device.getSequence_number())
                        .lastUpdate(device.getLastupdate())
                        .referenceKey(device.getReference_key())
                        .driverName(driverName)
                        .geofenceName(device.getGeofence().toString())
                        .driverId(driverId)
                        .driverPhoto(driverPhoto)
                        .driverUniqueId(driverUniqueId)
                        .plateType(device.getPlate_type().toString())
                        .plateNum(device.getPlate_num())
                        .rightLetter(device.getRight_letter())
                        .middleLetter(device.getMiddle_letter())
                        .leftLetter(device.getLeft_letter())
                        .ownerName(device.getOwner_name())
                        .ownerId(device.getOwner_id())
                        .userName(device.getUsername())
                        .brand(device.getBrand())
                        .model(device.getModel())
                        .madeYear(device.getMade_year())
                        .color(device.getColor())
                        .licenceExptDate(device.getLicense_exp())
                        .car_weight(device.getCar_weight())
                        .vehiclePlate(device.getPlate_num()+" "+device.getRight_letter()+" "+device.getMiddle_letter()+" "+device.getLeft_letter())
                        .positionId(device.getPositionid())
                        .latitude(latitude)
                        .longitude(longitude)
                        .speed(speed)
                        .address(address)
                        .attributes(attributes)
                        .driver_num(driverNum)
                        .lastPoints(lastPoints)
                        .build();
                builder.setEntity(result);
                builder.setMessage("Success");
                builder.setStatusCode(200);
                builder.setSuccess(true);
                builder.setSize(1);
                return builder.build();

            }else {
                builder.setEntity(null);
                builder.setMessage("This Vehicle Is Deleted or Not Found");
                builder.setSuccess(true);
                builder.setSize(0);
                builder.setStatusCode(200);
                return builder.build();
            }

    }

    @Override
    public ApiResponse<List<VehicleDetailsResponse>> getDevicesList(Long userId, int offset, String search) {

        ApiResponseBuilder<List<VehicleDetailsResponse>> builder = new ApiResponseBuilder<>();
        List<VehicleDetailsResponse> result = new ArrayList<>();
        List<Long> deviceIds; List<Integer> deviceIdsInt; List<Long> userIds; int listSize = 0;
        List<String> lastPositionIds; List<MongoPositions> mongoPositions = null;List<Driver> driverList = null;
        List<Long> driverIds;
        Optional<User> user = userRepository.findByIdAndDeleteDate(userId,null);
        if (user.isEmpty()) {
            throw new ApiGetException("User Is Not Found");
        }
        Optional<List<Device>> optionalDevicesList = Optional.empty();
        if (user.get().getAccountType().equals(1)) {
            deviceIdsInt = deviceRepository.getAllDeviceIds();
            deviceIds = deviceIdsInt.stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
                optionalDevicesList = deviceRepository.findAllByIdIn(deviceIds,PageRequest.of(offset,10));
            listSize = deviceIdsInt.size();
        } else if (user.get().getAccountType().equals(2)||user.get().getAccountType().equals(3)) {
            userIds = assistantServiceImpl.getUserChildrens(userId);
            deviceIdsInt = deviceRepository.clientDeviceIds(userIds);
            optionalDevicesList = deviceRepository.findAllByUser_idInAndDeleteDate(userIds,null,PageRequest.of(offset,10));
            listSize = deviceIdsInt.size();
        } else if (user.get().getAccountType().equals(4)) {
            Optional<List<UserClientDevice>> optionalUserClientDevice =
                    userClientDeviceReposiitory.findAllByUserid(userId);
            if (optionalUserClientDevice.isPresent()) {
                List<UserClientDevice> userDevice = optionalUserClientDevice.get();
                deviceIds = userDevice.stream()
                        .map(UserClientDevice::getDeviceid).collect(Collectors.toList());
                optionalDevicesList = deviceRepository.findAllByIdInAndDeleteDate(deviceIds,null);
                listSize = deviceIds.size();
            } else {
                builder.setStatusCode(200);
                builder.setEntity(null);
                builder.setMessage("Can`t Find Any Vehicles For This User");
                builder.setSize(0);
                builder.setSuccess(true);
                return builder.build();
            }
        }
        if (optionalDevicesList.isPresent()) {
            List<Device> deviceList = optionalDevicesList.get();
            if (!Objects.equals(search, "")) {
						if (deviceList.size() > 0 && Pattern.matches(".*\\S.*", search)) {
							deviceList = deviceList.stream().filter(device ->
									device.getName().contains(search)||device.getUniqueid().contains(search)||device.getSequence_number().contains(search)).collect(Collectors.toList());
						}
					}
            lastPositionIds = deviceList.stream()
                    .map(Device::getPositionid)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            driverIds = deviceList.stream()
                    .map(Device::getDriverId)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            Optional<List<Driver>> optionalDriverList = driverRepository.findAllByIdInAndDeleteDate(driverIds,null);
            if (optionalDriverList.isPresent()) {
                driverList = optionalDriverList.get();
            }
            Optional<List<MongoPositions>> optionalMongoPositionsList = mongoPositionsRepository.findAllBy_idIn(lastPositionIds);
            if (optionalMongoPositionsList.isPresent()) {
                mongoPositions = optionalMongoPositionsList.get();
            }
            for (Device device:deviceList) {

                Double latitude = null;Double longitude = null;Double speed = null;String address = null;
                String vehicleStatus;Double power = null ;Integer sat = null;Boolean ignition = null;
                Long driverId = null;String driverName = null; String driverNum = null; Long leftDays = null;
                Date now = new Date();String driverPhoto = null; String driverUniqueId = null;Object attributes = null;

                if (device.getEndDate() !=null){
                    leftDays = ChronoUnit.DAYS.between(now.toInstant(),device.getEndDate().toInstant());
                }

                vehicleStatus = assistantServiceImpl.getVehicleStatus(device.getLastupdate());

                if (device.getDriverId() != null) {
                    assert driverList != null;
                    driverId = driverList.stream().filter(driver -> driver.getId().equals(device.getDriverId())).findFirst().get().getId();
                    driverNum = driverList.stream().filter(driver -> driver.getId().equals(device.getDriverId())).findFirst().get().getMobile_num();
                    driverName = driverList.stream().filter(driver -> driver.getId().equals(device.getDriverId())).findFirst().get().getName();
                    driverPhoto = driverList.stream().filter(driver -> driver.getId().equals(device.getDriverId())).findFirst().get().getPhoto();
                    driverUniqueId = driverList.stream().filter(driver -> driver.getId().equals(device.getDriverId())).findFirst().get().getUniqueid();
                }

                try {
                    assert mongoPositions != null;
                    List<MongoPositions> mongoPosition = mongoPositions.stream()
                            .filter(mongoPositions1 -> mongoPositions1.getDeviceid().equals(device.getId())).toList();
                    if (!mongoPosition.isEmpty()){
                        attributes = mongoPosition.get(0).getAttributes();
                        speed = utilities.speedConverter(mongoPosition.get(0).getSpeed());
                        latitude = mongoPosition.get(0).getLatitude();
                        longitude = mongoPosition.get(0).getLongitude();
                        address = mongoPosition.get(0).getAddress();ObjectMapper mapper = new ObjectMapper();
                        String json = null;
                        try {
                            json = mapper.writeValueAsString(attributes);
                        } catch (JsonProcessingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        JSONObject obj = new JSONObject(json);

                        if(obj.has("power")) {
                           power = obj.getDouble("power");
                        }

                        if(obj.has("ignition")) {
                           ignition = obj.getBoolean("ignition");
                        }

                        if(obj.has("sat")) {
                            sat = obj.getInt("sat");
                        }
                    }
                }catch (Exception e){
                    throw new ApiGetException(e.getLocalizedMessage());
                }

                result.add(
                        VehicleDetailsResponse
                                .builder()
                                .id(device.getId())
                                .deviceName(device.getName())
                                .uniqueId(device.getUniqueid())
                                .sequenceNumber(device.getSequence_number())
                                .lastUpdate(device.getLastupdate())
                                .referenceKey(device.getReference_key())
                                .driverName(driverName)
                                .geofenceName(device.getGeofence().toString())
                                .driverId(driverId)
                                .driverPhoto(driverPhoto)
                                .driverUniqueId(driverUniqueId)
                                .plateType(String.valueOf(device.getPlate_type() !=null))
                                .plateNum(device.getPlate_num())
                                .rightLetter(device.getRight_letter())
                                .middleLetter(device.getMiddle_letter())
                                .leftLetter(device.getLeft_letter())
                                .ownerName(device.getOwner_name())
                                .ownerId(device.getOwner_id())
                                .userName(device.getUsername())
                                .brand(device.getBrand())
                                .model(device.getModel())
                                .madeYear(device.getMade_year())
                                .color(device.getColor())
                                .licenceExptDate(device.getLicense_exp())
                                .car_weight(device.getCar_weight())
                                .vehiclePlate(device.getPlate_num()+" "+device.getRight_letter()+" "+device.getMiddle_letter()+" "+device.getLeft_letter())
                                .positionId(device.getPositionid())
                                .latitude(latitude)
                                .longitude(longitude)
                                .speed(speed)
                                .address(address)
                                .attributes(attributes)
                                .driver_num(driverNum)
                                .leftDays(leftDays)
                                .status(vehicleStatus)
                                .ignition(ignition)
                                .power(power)
                                .sat(sat)
                                .build()
                );
            }

            builder.setEntity(result);
            builder.setMessage("Success");
            builder.setSuccess(true);
            builder.setSize(listSize);
            builder.setStatusCode(200);
            return builder.build();

        }else {
            builder.setEntity(result);
            builder.setMessage("No Vehicles Found");
            builder.setSuccess(true);
            builder.setSize(0);
            builder.setStatusCode(200);
            return builder.build();
        }
    }


}


