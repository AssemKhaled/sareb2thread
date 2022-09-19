package sarebApp.com.sareb.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import sarebApp.com.sareb.config.MultiThreading;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.responses.LoginResponse;
import sarebApp.com.sareb.entities.ThreadProcessedData;
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.exception.ApiRequestException;
import sarebApp.com.sareb.repository.ThreadProcessedDataRepository;
import sarebApp.com.sareb.service.Impl.LoginServiceImpl;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Assem
 */
@CrossOrigin
@RestController
@Slf4j
@RequestMapping(path = "/app")
public class LoginController {

    private final LoginServiceImpl loginServiceImpl;
    private final ThreadProcessedDataRepository threadProcessedDataRepository;
    public LoginController(LoginServiceImpl loginServiceImpl, ThreadProcessedDataRepository threadProcessedDataRepository) {
        this.loginServiceImpl = loginServiceImpl;
        this.threadProcessedDataRepository = threadProcessedDataRepository;
    }


    @GetMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> Login(@RequestHeader(value = "Authorization",defaultValue = "") String authorization){
        try{
            return ResponseEntity.ok(
                    loginServiceImpl.login(authorization)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<User>> test(@RequestParam(value = "Email",defaultValue = "") String email){
        try{
            return ResponseEntity.ok(
                    loginServiceImpl.getUser(email)
            );

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping("/getTrips")
    @Async("myTaskExecutor")
    public CompletableFuture<ResponseEntity<ApiResponse<List<User>>>> getTrips(@RequestParam(value = "deviceId",defaultValue = "") Long deviceId,
                                                                               @RequestParam(value = "from",defaultValue = "") String from,
                                                                               @RequestParam(value = "to",defaultValue = "") String to){

        try{
//            log.info("Execute method asynchronously" + Thread.currentThread().getName());
            //            service.submit((Callable<ApiResponse<?>>) new MultiThreading<List<User>>());
//            MultiThreading<ApiResponse<List<User>>>multiThreading = new MultiThreading<>();
//            List<Future<ApiResponse<?>>> futureList = new ArrayList<>();
//            for (int i = 0; i < 10; i++) {
//                futureList.add(future);
//            }
//            for (int i = 0; i < 10; i++) {
//                Future<ApiResponse<?>> future = futureList.get(i);

//            }
//            List<User> userList = CompletableFuture.completedFuture(Objects.requireNonNull(ResponseEntity.ok(
//                    loginServiceImpl.getTrips(deviceId, from, to)
//            ).getBody()).getEntity()).get();
//            CompletableFuture.allOf((CompletableFuture<?>) userList);
//            log.info(userList.toString()+"");
            return CompletableFuture.completedFuture(ResponseEntity.ok(
                    loginServiceImpl.getTrips(deviceId, from, to)
            ));

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }

    @GetMapping(value = "/getUsersByThread")
    @Async("myTaskExecutor")
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> getUsers(@RequestParam(value = "deviceId",defaultValue = "") Long deviceId,
                                                                      @RequestParam(value = "from",defaultValue = "") String from,
                                                                      @RequestParam(value = "to",defaultValue = "") String to) throws ExecutionException, InterruptedException, JsonProcessingException {
        ApiResponse<List<User>> apiResponse = new ApiResponse<>();String futureStatus;
        ObjectMapper oMapper = new ObjectMapper();
        Map<Long, Object> newObject;
        ExecutorService service = Executors.newFixedThreadPool(2);
        MultiThreading multiThreading = new MultiThreading(deviceId,from,to,apiResponse, loginServiceImpl);
        Future<ApiResponse<?>> future = service.submit((Callable<ApiResponse<?>>) multiThreading);
//        List<User> userList = (List<User>) apiResponse2.getEntity();
//        log.info(userList.toString()+"--"+Thread.currentThread().getName());
        ApiResponse<?> apiResponse2 = future.get();
        if (future.isDone()) {
            futureStatus = "Finished :"+Thread.currentThread().getName();
            List<User> userList = (List<User>) apiResponse2.getEntity();
//            newObject = oMapper.convertValue(apiResponse2.getEntity(),Map.class);
            newObject = userList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
            threadProcessedDataRepository.save(ThreadProcessedData.
                    builder()
                    .entity(newObject)
                    .size(apiResponse2.getSize())
                    .message(apiResponse2.getMessage())
                    .success(apiResponse2.getSuccess())
                    .statusCode(apiResponse2.getStatusCode())
                    .threadStatus(futureStatus)
                    .build());
        }
        else if (future.isCancelled()) {
            futureStatus="Failed :"+Thread.currentThread().getName();
            threadProcessedDataRepository.save(ThreadProcessedData.
                    builder()
                    .entity(null)
                    .size(null)
                    .message(apiResponse2.getMessage())
                    .success(apiResponse2.getSuccess())
                    .statusCode(apiResponse2.getStatusCode())
                    .threadStatus(futureStatus)
                    .build());
        }
        try{
            return CompletableFuture.completedFuture(ResponseEntity.ok(
                    loginServiceImpl.getTrips(deviceId, from, to)
            ));

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }


}
