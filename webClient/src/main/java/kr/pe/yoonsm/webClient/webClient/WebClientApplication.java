package kr.pe.yoonsm.webClient.webClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication
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
}
