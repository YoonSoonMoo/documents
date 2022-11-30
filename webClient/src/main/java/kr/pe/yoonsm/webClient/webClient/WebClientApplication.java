package kr.pe.yoonsm.webClient.webClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


@SpringBootApplication
@EnableAsync
public class WebClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebClientApplication.class, args);
    }

    @Bean("WebClient")
    public WebClient makeWebClinet() {
        return WebClient.builder().build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public Executor asyncThreadTaskExecutor() {
//        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
//        threadPoolTaskExecutor.setCorePoolSize(100);
//        threadPoolTaskExecutor.setMaxPoolSize(200);
//        threadPoolTaskExecutor.setQueueCapacity(300);
//        threadPoolTaskExecutor.setThreadNamePrefix("thread-pool");
//        return threadPoolTaskExecutor;
//    }

    @Bean
    public ExecutorService createExecutorService(){
        ExecutorService executor = Executors.newFixedThreadPool(10, new ThreadFactory() {
            int count = 1;
            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "custom-executor-" + count++);
            }
        });
        return executor;
    }

}
