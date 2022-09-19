package sarebApp.com.sareb.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.ApiResponseBuilder;
import sarebApp.com.sareb.dto.responses.DriverSelectResponse;
import sarebApp.com.sareb.entities.*;
import sarebApp.com.sareb.exception.ApiGetException;
import sarebApp.com.sareb.helper.Impl.AssistantServiceImpl;
import sarebApp.com.sareb.helper.Impl.Utilities;
import sarebApp.com.sareb.repository.*;
import sarebApp.com.sareb.service.DriverService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Assem
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final UserRepository userRepository;
    private final AssistantServiceImpl assistantServiceImpl;
    private final UserClientDriverRepository userClientDriverRepository;
    private final DriverRepository driverRepository;


    @Override
    public ApiResponse<List<Driver>> getDriverList(Long userId, int offset, String search) {
        log.info("*********************** GET Driver LIST STARTED ***********************");
        ApiResponseBuilder<List<Driver>> builder = new ApiResponseBuilder<>();
        Optional<List<Driver>> optionalDriverList; List<Driver> result = new ArrayList<>();
        List<Long> driverIds; List<Integer> driverIdsInt;List<Long> userIds;int listSize = 0;
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getAccountType().equals(1)) {
                driverIdsInt = driverRepository.getAllDriverIds();
                driverIds = driverIdsInt.stream().map(Integer::longValue).collect(Collectors.toList());
                optionalDriverList = driverRepository.findAllByIdIn(driverIds, PageRequest.of(offset,10));
                if (!Objects.equals(search, "")){
                    Page<Driver> driverPage;
                    driverPage = searchDriver(offset,search,driverIds,1);
                    optionalDriverList = Optional.of(driverPage.stream().toList());
                    listSize = (int) driverPage.getTotalElements();
                }else {
                    listSize = driverIdsInt.size();
                }
            } else if (user.getAccountType().equals(2)||user.getAccountType().equals(3)) {
                userIds = assistantServiceImpl.getUserChildrens(userId);
                driverIdsInt = driverRepository.clientDriverIds(userIds);
                optionalDriverList = driverRepository.findAllByUserIdInAndDeleteDate(userIds,null,PageRequest.of(offset,10));
                if (!Objects.equals(search, "")){
                    Page<Driver> driverPage;
                    driverPage = searchDriver(offset,search,userIds,23);
                    optionalDriverList = Optional.of(driverPage.stream().toList());
                    listSize = (int) driverPage.getTotalElements();
                }else {
                    listSize = driverIdsInt.size();
                }
            }else {
                Optional<List<UserClientDriver>> optionalUserClientDrivers =
                        userClientDriverRepository.findAllByUserid(userId);
                if (optionalUserClientDrivers.isPresent()) {
                    List<UserClientDriver> userDevice = optionalUserClientDrivers.get();
                    driverIds = userDevice.stream()
                            .map(UserClientDriver::getDriverid)
                            .collect(Collectors.toList());
                    optionalDriverList = driverRepository.findAllByIdInAndDeleteDate(driverIds,null);
                    listSize = driverIds.size();
                }else {
                    builder.setStatusCode(200);
                    builder.setEntity(null);
                    builder.setMessage("Can`t Find Any Vehicles For This User");
                    builder.setSize(0);
                    builder.setSuccess(true);
                    return builder.build();
                }
            }
            if (optionalDriverList.isPresent()) {
                List<Driver> driverList = optionalDriverList.get();
                result.addAll(driverList);

                builder.setEntity(result);
                builder.setMessage("Success");
                builder.setSuccess(true);
                builder.setSize(listSize);
            }else {
                builder.setEntity(result);
                builder.setMessage("No Drivers Found");
                builder.setSuccess(true);
                builder.setSize(0);
            }
            log.info("*********************** GET Driver LIST ENDED ***********************");
            builder.setStatusCode(200);
            return builder.build();
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
    public ApiResponse<Driver> getDriverById(Long userId, Long driverId) {
        log.info("*********************** Get Device By Id Started ***********************");
        ApiResponseBuilder<Driver> builder = new ApiResponseBuilder<>();
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Driver> optionalDriver = Optional.empty(); Optional<UserClientDriver> optionalUserClientDriver ;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getAccountType().equals(1)) {
                optionalDriver = driverRepository.findByIdAndDeleteDate(driverId,null);
            } else if (user.getAccountType().equals(2) || user.getAccountType().equals(3)) {
                optionalDriver = driverRepository.findByUserIdAndDeleteDate(userId,null);
            }else {
                optionalUserClientDriver = userClientDriverRepository.findByUseridAndDriverid(userId,driverId);
                if (optionalUserClientDriver.isPresent()) {
                    UserClientDriver userDevice = optionalUserClientDriver.get();
                    optionalDriver = driverRepository.findByIdAndDeleteDate(userDevice.getDriverid(),null);
                }
            }
            if (optionalDriver.isPresent()) {
                Driver driver = optionalDriver.get();
                builder.setStatusCode(200);
                builder.setEntity(driver);
                builder.setMessage("Success");
                builder.setSize(1);
                builder.setSuccess(true);
                return builder.build();
            }else {
                builder.setStatusCode(200);
                builder.setEntity(null);
                builder.setMessage("Can`t Find Any Drivers");
                builder.setSize(0);
                builder.setSuccess(true);
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
    public ApiResponse<List<DriverSelectResponse>> getDriverSelect(Long userId) {
        log.info("*********************** GET Driver SELECT STARTED ***********************");
        ApiResponseBuilder<List<DriverSelectResponse>> builder = new ApiResponseBuilder<>();
        Optional<User> optionalUser = userRepository.findByIdAndDeleteDate(userId,null);
        List<DriverSelectResponse> result = new ArrayList<>(); Optional<List<Driver>> optionalDriverList; List<Long> userIds;
        Optional<List<UserClientDriver>> optionalUserClientDriver; List<Long> driverIds;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getAccountType().equals(1)) {
                optionalDriverList = driverRepository.findAllByDeleteDateIsNull();
            } else if (user.getAccountType().equals(2) || user.getAccountType().equals(3)) {
                userIds = assistantServiceImpl.getUserChildrens(userId);
                optionalDriverList = driverRepository.findAllByUserIdInAndDeleteDate(userIds,null);
            } else {
                optionalUserClientDriver = userClientDriverRepository.findAllByUserid(userId);
                if (optionalUserClientDriver.isPresent()) {
                    List<UserClientDriver> userDevice = optionalUserClientDriver.get();
                    driverIds = userDevice.stream()
                            .map(UserClientDriver::getDriverid)
                            .collect(Collectors.toList());
                    optionalDriverList = driverRepository.findAllByIdInAndDeleteDate(driverIds,null);
                }else {
                    builder.setStatusCode(200);
                    builder.setEntity(null);
                    builder.setMessage("Can`t Find Any Drivers For This User");
                    builder.setSize(0);
                    builder.setSuccess(true);
                    return builder.build();
                }
            }
            if (optionalDriverList.isPresent()) {
                List<Driver> driverList = optionalDriverList.get();
                for (Driver driver:driverList) {
                    result.add(
                            DriverSelectResponse
                                    .builder()
                                    .id(driver.getId())
                                    .name(driver.getName())
                                    .build()
                    );
                }
                builder.setEntity(result);
                builder.setMessage("Success");
                builder.setSuccess(true);
                builder.setSize(result.size());
            }else {
                builder.setEntity(null);
                builder.setMessage("No Drivers Found");
                builder.setSuccess(true);
                builder.setSize(0);
            }
            log.info("*********************** GET Driver SELECT ENDED ***********************");
            builder.setStatusCode(200);
            return builder.build();
        } else {
            builder.setEntity(null);
            builder.setMessage("User Not Found");
            builder.setSuccess(true);
            builder.setSize(0);
            builder.setStatusCode(200);
            return builder.build();
        }
    }

    @Override
    public Page<Driver> searchDriver(int offset, String search, List<Long> ids, int type) {
        log.info("*********************** Search Driver ENDED ***********************");
        Page<Driver> driverList;
        if (type == 1) {
            driverList = driverRepository.AdminDriverListSearch(ids,search,PageRequest.of(offset,10));
        }else{
            driverList = driverRepository.DriverListSearch(ids,search,PageRequest.of(offset,10));
        }
        log.info("*********************** Search Driver ENDED ***********************");
        if (!driverList.isEmpty()) {
            return driverList;
        }else {
            throw new ApiGetException("No Matches Found");
        }
    }


}
