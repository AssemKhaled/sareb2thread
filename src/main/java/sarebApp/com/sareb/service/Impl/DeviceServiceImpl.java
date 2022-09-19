package sarebApp.com.sareb.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.ApiResponseBuilder;
import sarebApp.com.sareb.dto.responses.*;
import sarebApp.com.sareb.entities.*;
import sarebApp.com.sareb.exception.ApiGetException;
import sarebApp.com.sareb.helper.Impl.AssistantServiceImpl;
import sarebApp.com.sareb.helper.Impl.Utilities;
import sarebApp.com.sareb.repository.*;
import sarebApp.com.sareb.service.DeviceService;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Assem
 */
@Service
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final AssistantServiceImpl assistantServiceImpl;
    private final MongoPositionsRepository mongoPositionsRepository;
    private final UserClientDeviceRepository userClientDeviceRepository;
    private final DriverRepository driverRepository;
    private final Utilities utilities;


    @Override
    public ApiResponse<List<DevicesMapResponse>> getDeviceLiveDataMap(Long userId) {
        log.info("*********************** GET DEVICE LIVE DATA MAP STARTED ***********************");
        ApiResponseBuilder<List<DevicesMapResponse>> builder = new ApiResponseBuilder<>();
        List<DevicesMapResponse> result = new ArrayList<>() ;List<Long> userIds;List<Long> deviceIds;
        Optional<List<Device>> optionalNoPositionDeviceList = Optional.empty();
        Optional<List<Device>> optionalPositionDeviceList = Optional.empty();
        Optional<List<MongoPositions>> optionalMongoPositionsList;
        List<String> positionIdsOffline = new ArrayList<>();List<String> positionIdsOutOfNetwork= new ArrayList<>();
        List<String> positionIdsOnline= new ArrayList<>();
        Optional<User> user = userRepository.findByIdAndDeleteDate(userId,null);
        if (user.isEmpty()) {
            throw new ApiGetException("User Is Not Found");
        }
        if(user.get().getAccountType().equals(1)){
            optionalNoPositionDeviceList = deviceRepository.findAllByDeleteDateIsNullAndPositionidIsNull();
            optionalPositionDeviceList = deviceRepository.findAllByDeleteDateIsNullAndPositionidIsNotNull();
        }else if (user.get().getAccountType().equals(2)||user.get().getAccountType().equals(3)){
            userIds = assistantServiceImpl.getUserChildrens(userId);
            optionalNoPositionDeviceList = deviceRepository.findAllByUser_idInAndDeleteDateIsNullAndPositionidIsNull(userIds);
            optionalPositionDeviceList = deviceRepository.findAllByUser_idInAndDeleteDateIsNullAndPositionidIsNotNull(userIds);
        } else if (user.get().getAccountType().equals(4)) {
            Optional<List<UserClientDevice>> optionalUserClientDevice =
                    userClientDeviceRepository.findAllByUserid(userId);
            if (optionalUserClientDevice.isPresent()) {
                List<UserClientDevice> userDevice = optionalUserClientDevice.get();
                deviceIds = userDevice.stream()
                        .map(UserClientDevice::getDeviceid)
                        .collect(Collectors.toList());
                optionalNoPositionDeviceList = deviceRepository.findAllByIdInAndDeleteDateIsNullAndPositionidIsNull(deviceIds);
                optionalPositionDeviceList = deviceRepository.findAllByIdInAndDeleteDateIsNullAndPositionidIsNotNull(deviceIds);
            } else {
                throw new ApiGetException("Can`t Find Any Vehicles For This User");
            }
        }
        if (optionalNoPositionDeviceList.isPresent()){
            List<Device> deviceList = optionalNoPositionDeviceList.get();
            for (Device device:deviceList) {
                result.add(
                        DevicesMapResponse
                                .builder()
                                .id(device.getId())
                                .deviceName(device.getName())
                                .vehicleStatus(3)
                                .longitude(null)
                                .latitude(null)
                                .course(null)
                                .build()
                );
            }
        }
        if (optionalPositionDeviceList.isPresent()) {
            List<Device> deviceList = optionalPositionDeviceList.get();
            for (Device device:deviceList) {
                String status;
                status = assistantServiceImpl.getVehicleStatus(device.getLastupdate());
                if (Objects.equals(status, "offline")) {
                    positionIdsOffline.add(device.getPositionid());
                } else if (Objects.equals(status, "online")) {
                    positionIdsOnline.add(device.getPositionid());
                } else if (Objects.equals(status, "unknown")) {
                    positionIdsOutOfNetwork.add(device.getPositionid());
                }
            }
            if (positionIdsOffline.size() > 0) {
                List<DevicesMapResponse> allDevicesPositionOfflineResponse = new ArrayList<>();
                optionalMongoPositionsList = mongoPositionsRepository.findAllBy_idIn(positionIdsOffline);
                if (optionalMongoPositionsList.isPresent()) {
                    List<MongoPositions> mongoPositionsList = optionalMongoPositionsList.get();
                    for (MongoPositions mongoPositions:mongoPositionsList) {
                        allDevicesPositionOfflineResponse.add(
                                DevicesMapResponse
                                        .builder()
                                        .id(mongoPositions.getDeviceid())
                                        .deviceName(mongoPositions.getDeviceName())
                                        .vehicleStatus(3)
                                        .longitude(mongoPositions.getLongitude())
                                        .latitude(mongoPositions.getLatitude())
                                        .course(mongoPositions.getCourse())
                                        .build()
                        );
                    }
                    result.addAll(allDevicesPositionOfflineResponse);
                }
            }
            if (positionIdsOutOfNetwork.size() > 0) {
                List<DevicesMapResponse> allDevicesPositionOutOfNetwork = new ArrayList<>();
                optionalMongoPositionsList = mongoPositionsRepository.findAllBy_idIn(positionIdsOutOfNetwork);
                if (optionalMongoPositionsList.isPresent()) {
                    List<MongoPositions> mongoPositionsList = optionalMongoPositionsList.get();
                    for (MongoPositions mongoPositions:mongoPositionsList) {
                        allDevicesPositionOutOfNetwork.add(
                                DevicesMapResponse
                                        .builder()
                                        .id(mongoPositions.getDeviceid())
                                        .deviceName(mongoPositions.getDeviceName())
                                        .vehicleStatus(2)
                                        .longitude(mongoPositions.getLongitude())
                                        .latitude(mongoPositions.getLatitude())
                                        .course(mongoPositions.getCourse())
                                        .build()
                        );
                    }
                    result.addAll(allDevicesPositionOutOfNetwork);
                }
            }
            if (positionIdsOnline.size() > 0) {
                List<DevicesMapResponse> allDevicesPositionOnline = new ArrayList<>();
                optionalMongoPositionsList = mongoPositionsRepository.findAllBy_idIn(positionIdsOnline);
                if (optionalMongoPositionsList.isPresent()) {
                    List<MongoPositions> mongoPositionsList = optionalMongoPositionsList.get();
                    for (MongoPositions mongoPositions:mongoPositionsList) {
                        allDevicesPositionOnline.add(
                                DevicesMapResponse
                                        .builder()
                                        .id(mongoPositions.getDeviceid())
                                        .deviceName(mongoPositions.getDeviceName())
                                        .vehicleStatus(1)
                                        .longitude(mongoPositions.getLongitude())
                                        .latitude(mongoPositions.getLatitude())
                                        .course(mongoPositions.getCourse())
                                        .build()
                        );
                    }
                    result.addAll(allDevicesPositionOnline);
                }
            }

        }

        log.info("*********************** GET DEVICE LIVE DATA MAP ENDED ***********************");
        builder.setEntity(result);
        builder.setMessage("Success");
        builder.setSuccess(true);
        builder.setSize(result.size());
        builder.setStatusCode(200);
        return builder.build();
    }

    @Override
    public ApiResponse<VehicleDetailsResponse> getDeviceDetails(Long userId, Long deviceId) {
        log.info("*********************** GET DEVICE DETAILS STARTED ***********************");
        ApiResponseBuilder<VehicleDetailsResponse> builder = new ApiResponseBuilder<>();
        Optional<UserClientDevice> optionalUserClientDevice ;
        Map<Object, Object> sensorList = new HashMap<>();
//        ArrayList<Map<Object,Object>> /lastPoints = new ArrayList<>();
        MongoMapperResponse mongoMapper ;
        VehicleDetailsResponse result ;
        Double latitude = null;Double longitude = null;Double speed = null;String address = null;Long id;
        Object attributes = null; Long driverId; String driverPhoto = null; String driverUniqueId = null;
        String driverName = null; String driverNum = null;Double course = null; String status = null;
        Double power = null ;Integer sat = null;Boolean ignition = null;Double operator=null;
        Optional<User> user = userRepository.findByIdAndDeleteDate(userId,null);
        if (user.isEmpty()) {
            throw new ApiGetException("User Is Not Found");
        }
        Optional<Device> optionalDevice ;
        if (user.get().getAccountType().equals(1) || user.get().getAccountType().equals(2)){
            optionalDevice = deviceRepository.findByIdAndDeleteDate(deviceId,null);
        } else if (user.get().getAccountType().equals(3)) {
            optionalDevice = deviceRepository.findByIdAndUser_idAndDeleteDateIsNull(deviceId,userId);
        } else {
            optionalUserClientDevice = userClientDeviceRepository.findByUseridAndDeviceid(userId,deviceId);
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
                status = assistantServiceImpl.getVehicleStatus(device.getLastupdate());
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
                        course = mongoPositions.getCourse();
                        address = mongoPositions.getAddress();
                        speed = mongoPositions.getSpeed()*(1.852);
                        mongoMapper = utilities.MongoObjJsonMapper(mongoPositions.getAttributes(),speed,device.getId(),device.getLastupdate());
                        attributes = mongoMapper.getAttributes();
//                        lastPoints = mongoMapper.getLastPoints();
                        ObjectMapper mapper = new ObjectMapper();
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
                        if(obj.has("operator")) {
                            operator = obj.getDouble("operator");
                        }
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
                        .course(course)
                        .speed(speed)
                        .address(address)
                        .attributes(attributes)
                        .driver_num(driverNum)
                        .status(status)
                        .sat(sat)
                        .operator(operator)
                        .power(power)
                        .ignition(ignition)
                        .build();
                log.info("*********************** GET DEVICE DETAILS ENDED ***********************");
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
        log.info("*********************** GET DEVICE LIST STARTED ***********************");
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
            if (!Objects.equals(search, "")){
                Page<Device> devicePage ;
                devicePage = searchDevice(offset,search,deviceIds,1);
                optionalDevicesList = Optional.of(devicePage.stream().toList());
                listSize = (int) devicePage.getTotalElements();
            }else {
                listSize = deviceIdsInt.size();
            }
        } else if (user.get().getAccountType().equals(2)||user.get().getAccountType().equals(3)) {
            userIds = assistantServiceImpl.getUserChildrens(userId);
            deviceIdsInt = deviceRepository.clientDeviceIds(userIds);
            optionalDevicesList = deviceRepository.findAllByUser_idInAndDeleteDate(userIds,null,PageRequest.of(offset,10));
            if (!Objects.equals(search,"")){
//                optionalDevicesList = Optional.ofNullable(searchDevice(offset,search,userIds,23));
                Page<Device> devicePage ;
                devicePage = searchDevice(offset,search,userIds,23);
                optionalDevicesList = Optional.of(devicePage.stream().toList());
                listSize = (int) devicePage.getTotalElements();
            }else {
                listSize = deviceIdsInt.size();
            }
        } else if (user.get().getAccountType().equals(4)) {
            Optional<List<UserClientDevice>> optionalUserClientDevice =
                    userClientDeviceRepository.findAllByUserid(userId);
            if (optionalUserClientDevice.isPresent()) {
                List<UserClientDevice> userDevice = optionalUserClientDevice.get();
                deviceIds = userDevice.stream()
                        .map(UserClientDevice::getDeviceid)
                        .collect(Collectors.toList());
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
//            if (!Objects.equals(search, "")) {
//						if (deviceList.size() > 0 && Pattern.matches(".*\\S.*", search)) {
//							deviceList = deviceList.stream().filter(device ->
//									device.getName().contains(search)||device.getUniqueid().contains(search)||device.getSequence_number().contains(search)).collect(Collectors.toList());
//						}
//					}
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
                Double course = null;

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
                        course = mongoPosition.get(0).getCourse();
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
                                .course(course)
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
            log.info("*********************** GET DEVICE LIST ENDED ***********************");
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

    @Override
    public Page<Device> searchDevice(int offset, String search,List<Long> ids,int type) {
        log.info("*********************** Search Device ENDED ***********************");
        Page<Device> deviceList;
        if (type == 1) {
            deviceList = deviceRepository.AdminDeviceListSearch(ids,search,PageRequest.of(offset,10));
        }else{
            deviceList = deviceRepository.DeviceListSearch(ids,search,PageRequest.of(offset,10));
        }
        log.info("*********************** Search Device ENDED ***********************");
        if (!deviceList.isEmpty()) {
            return deviceList;
        }else {
         throw new ApiGetException("No Matches Found");
        }
    }

    @Override
    public ApiResponse<Device> getDeviceById(Long userId, Long deviceId) {
        log.info("*********************** Get Device By Id Started ***********************");
        ApiResponseBuilder<Device> builder = new ApiResponseBuilder<>();
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Device> optionalDevice = Optional.empty();Optional<UserClientDevice> optionalUserClientDevice ;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getAccountType().equals(1)) {
                optionalDevice = deviceRepository.findByIdAndDeleteDate(deviceId,null);
            } else if (user.getAccountType().equals(2) || user.getAccountType().equals(3)) {
                optionalDevice = deviceRepository.findByIdAndUser_idAndDeleteDateIsNull(deviceId,userId);
            }else {
                optionalUserClientDevice = userClientDeviceRepository.findByUseridAndDeviceid(userId,deviceId);
                if (optionalUserClientDevice.isPresent()) {
                    UserClientDevice userDevice = optionalUserClientDevice.get();
                    optionalDevice = deviceRepository.findByIdAndDeleteDate(userDevice.getDeviceid(),null);
                }
            }
            if (optionalDevice.isPresent()) {
                Device device = optionalDevice.get();
                builder.setStatusCode(200);
                builder.setEntity(device);
                builder.setMessage("Success");
                builder.setSize(1);
                builder.setSuccess(true);
                return builder.build();
            }else {
                builder.setStatusCode(200);
                builder.setEntity(null);
                builder.setMessage("Can`t Find Any Vehicles");
                builder.setSize(0);
                builder.setSuccess(false);
                return builder.build();
            }
        }else {
            builder.setEntity(null);
            builder.setMessage("User Not Found");
            builder.setSuccess(false);
            builder.setSize(0);
            builder.setStatusCode(200);
            return builder.build();
        }
    }

    @Override
    public ApiResponse<Driver> getDeviceDriver(Long userId, Long deviceId) {
        log.info("*********************** Get Device Driver Started ***********************");
        ApiResponseBuilder<Driver> builder = new ApiResponseBuilder<>();
        Optional<Driver> optionalDriver;
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Optional<Device> optionalDevice = deviceRepository.findByIdAndDeleteDate(deviceId, null);
            if (optionalDevice.isPresent()) {
                Device device = optionalDevice.get();
                if(device.getDriverId() == null) {
                    builder.setEntity(null);
                    builder.setMessage("No drivers assigned to this device");
                    builder.setSuccess(true);
                    builder.setSize(0);
                    builder.setStatusCode(200);
                    return builder.build();
                } else {
                    optionalDriver = driverRepository.findById(device.getDriverId());
                    if (optionalDriver.isPresent()) {
                        Driver driver = optionalDriver.get();
                        builder.setEntity(driver);
                        builder.setMessage("Success");
                        builder.setSuccess(true);
                        builder.setSize(1);
                        builder.setStatusCode(200);
                        return builder.build();
                    }else {
                        builder.setEntity(null);
                        builder.setMessage("Driver Assigned Not Found OR Deleted");
                        builder.setSuccess(true);
                        builder.setSize(0);
                        builder.setStatusCode(200);
                        return builder.build();
                    }
                }
            }else {
                builder.setEntity(null);
                builder.setMessage("This device is not found");
                builder.setSuccess(true);
                builder.setSize(0);
                builder.setStatusCode(200);
                return builder.build();
            }
        }else {
            builder.setEntity(null);
            builder.setMessage("User Not Found");
            builder.setSuccess(true);
            builder.setSize(0);
            builder.setStatusCode(200);
            return builder.build();
        }
    }

    @Override
    public ApiResponse<List<GetDeviceSelectResponse>> getDeviceSelect(Long userId) {
        ApiResponseBuilder<List<GetDeviceSelectResponse>> builder = new ApiResponseBuilder<>();
        List<GetDeviceSelectResponse> result = new ArrayList<>();
        List<DeviceSelect>devices=new ArrayList<>();
        User loggedUser=null;
        List<Long>ids;
        if (userId!=0){

            Optional<User> user=userRepository.findById(userId);
            if (!user.isPresent()){
                builder.setMessage("User is Not Found");
                builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                builder.setEntity(null);
                builder.setSize(0);
                builder.setSuccess(false);
                return builder.build();
            }
            if (user.isPresent()){
                loggedUser=user.get();
                if (loggedUser.getDeleteDate()!=null){
                    builder.setMessage("User is Not Found");
                    builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                    builder.setSize(0);
                    builder.setSuccess(false);
                    return builder.build();
                }
            }
            if (user.isPresent()){
                loggedUser=user.get();
                if (loggedUser.getDeleteDate()==null){
                    if (loggedUser.getAccountType().equals(4)){
                        List<Long> deviceIds=userClientDeviceRepository.getDevicesIds(userId);
                        if (deviceIds.size()>0){
                            devices=deviceRepository.getDeviceSelectByIds(deviceIds);
                        }

                    }

                }
                if(loggedUser.getAccountType().equals(1)){
                    List<Integer>DeviceIds=deviceRepository.getAllDeviceIds();
                    ids = DeviceIds.stream()
                            .map(Integer::longValue)
                            .collect(Collectors.toList());
                    devices=deviceRepository.getDeviceSelectByIds(ids);
                }
                if(loggedUser.getAccountType().equals(2)){
                    List<Long>usersIds = assistantServiceImpl.getUserChildrens(userId);
                    List<Integer>userDeviceIds = deviceRepository.deviceIdsByUserChildrens(usersIds);
                    ids = userDeviceIds.stream()
                            .map(Integer::longValue)
                            .collect(Collectors.toList());
                    devices=deviceRepository.getDeviceSelectByIds(ids);
                }
                if(loggedUser.getAccountType().equals(3)){
                    devices=deviceRepository.getDeviceSelectByIdsclient(userId);
                }
                for(DeviceSelect device:devices) {
                    result.add(
                            GetDeviceSelectResponse.builder()
                                    .id(device.getId())
                                    .name(device.getName())
                                    .build()
                    );
                }
            }
        }

        builder.setMessage("success");
        builder.setStatusCode(HttpStatus.OK.value());
        builder.setEntity(result);
        builder.setSize(result.size());
        builder.setSuccess(true);
        return builder.build();

    }

    @Override
    public ApiResponse<DeviceResponse> changeIcon(Long deviceId, Long userId, String icon) {
        ApiResponseBuilder<DeviceResponse>builder=new ApiResponseBuilder<>();
        User loggeduser=null;
        Device deviceinfo=null;
        if(userId!=0){
            Optional<User> user=userRepository.findById(userId);
            if(user.isPresent()){
                Optional<Device> device=deviceRepository.findById(deviceId);
                if(device.isPresent()){
                    if(!icon.equals("")) {
                        deviceinfo=device.get();
                        deviceinfo.setIcon(icon);
                        deviceRepository.save(deviceinfo);
                        builder.setMessage("success");
                        builder.setStatusCode(HttpStatus.OK.value());
                        builder.setEntity(DeviceResponse.builder().id(deviceinfo.getId()).name(deviceinfo.getName())
                                .uniqueid(deviceinfo.getUniqueid()).lastupdate(deviceinfo.getLastupdate()).positionid(deviceinfo.getPositionid()).phone(deviceinfo.getPhone())
                                .model(deviceinfo.getModel()).plate_num(deviceinfo.getPlate_num()).right_letter(deviceinfo.getRight_letter()).left_letter(deviceinfo.getLeft_letter())
                                .plate_type(deviceinfo.getPlate_type()).brand(deviceinfo.getBrand()).color(deviceinfo.getColor()).photo(deviceinfo.getPhoto()).startDate(deviceinfo.getStart_date())
                                .endDate(deviceinfo.getEndDate()).icon(deviceinfo.getIcon())
                                .build());
                        builder.setSize(0);
                        builder.setSuccess(true);
                        return builder.build();
                    }else {
                        builder.setMessage("icon is required");
                        builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        builder.setEntity(null);
                        builder.setSize(0);
                        builder.setSuccess(false);
                        return builder.build();
                    }
                }else {
                    builder.setMessage("Device is Not Found");
                    builder.setStatusCode(HttpStatus.NOT_FOUND.value());
                    builder.setEntity(null);
                    builder.setSize(0);
                    builder.setSuccess(false);
                    return builder.build();
                }
            }
            else {
                builder.setMessage("User is Not Found");
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

    }


}


