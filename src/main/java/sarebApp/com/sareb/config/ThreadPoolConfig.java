package sarebApp.com.sareb.config;

/**
 * @author Assem
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolConfig  {

    @Bean(name = "myTaskExecutor")
    public Executor taskExecutor() {
        log.debug("Creating Async Thread Task Executor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(10);
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setThreadNamePrefix("ON THREAD:"+Thread.currentThread().getName());
        executor.setRejectedExecutionHandler((r, executor1) -> {
            try {
                executor1.getQueue().put(r);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        executor.initialize();
        return executor;
    }

}
//    private final ThreadPoolExecutor threadPoolExecutor;
//
//    public ThreadPoolConfig()
//    {
//        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(10000);
//        threadPoolExecutor = new ThreadPoolExecutor(2,
//                10,
//                20,
//                TimeUnit.SECONDS, blockingQueue);
//        threadPoolExecutor.setRejectedExecutionHandler((r, executor) ->
//        {
//            try
//            {
//                Thread.sleep(1000);
//                log.error("Exception occurred while adding task, Waiting for some time");
//            }
//            catch (InterruptedException e)
//            {
//                log.error("Thread interrupted:  ()",e.getCause());
//                Thread.currentThread().interrupt();
//            }
//            threadPoolExecutor.execute(r);
//        });
//    }
//
//    public void executeTask(TaskThread taskThread)
//    {
//        Future<?> future=threadPoolExecutor.submit(taskThread);
//        System.out.println("Queue Size: "+threadPoolExecutor.getQueue().size());
//        System.out.println("Number of Active Threads: "+threadPoolExecutor.getActiveCount());
//    }
