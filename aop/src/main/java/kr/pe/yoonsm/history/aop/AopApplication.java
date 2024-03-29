package kr.pe.yoonsm.history.aop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AopApplication {
    public static void main(String[] args) {
        SpringApplication.run(AopApplication.class, args);
    }

}
