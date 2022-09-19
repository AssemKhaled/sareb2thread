package sarebApp.com.sareb.config;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.service.Impl.LoginServiceImpl;

import java.util.concurrent.Callable;

/**
 * @author Assem
 */
@Getter
@Setter
@Slf4j
public class MultiThreading implements Runnable , Callable<ApiResponse<?>> {
    
    private Long deviceId;
    private String from;
    private String to;
    private ApiResponse<?> apiResponse;
    private final LoginServiceImpl loginServiceImpl;
    public MultiThreading(Long deviceId, String from, String to, ApiResponse<?> apiResponse, LoginServiceImpl loginServiceImpl) {
        this.deviceId = deviceId;
        this.from = from;
        this.to = to;
        this.apiResponse = apiResponse;
        this.loginServiceImpl = loginServiceImpl;
    }

    @Override
    public void run(){
//      log.info("THREAD USED IS :"+threadNum);
      log.info("THREAD USED IS :"+Thread.currentThread().getName());
    }

    @Override
    public ApiResponse<?> call() throws Exception {
        Thread.sleep(3000);
        return  loginServiceImpl.getTrips(deviceId,from,to);
    }
}
