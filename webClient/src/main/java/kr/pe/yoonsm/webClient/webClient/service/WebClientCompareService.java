package kr.pe.yoonsm.webClient.webClient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-28
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientCompareService {

    private final WebClient webClient;
    private final RestTemplate restTemplate;

    public void webClientConnect() {

        StopWatch stopWatch = new StopWatch();
        log.info("---- webClientConnect start!");
        stopWatch.start();

        Mono<String> resultFor3Sec = webClient.get()
                .uri("http://localhost:8080/wait3sec", String.class)
                .retrieve()
                .bodyToMono(String.class);

        resultFor3Sec.subscribe(result -> {
            log.info("resultFor3Sec: {}", result);
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            log.info("Total result(3Sec): {}", stopWatch.getTotalTimeMillis());
        });

        log.info("---- webClientConnect over!! ");
    }

    public void restTemplateConnect() {
        StopWatch stopWatch = new StopWatch();
        log.info("---- restTemplateConnect start!");
        stopWatch.start();
        String result = restTemplate.getForObject("http://localhost:8080/wait3sec", String.class);
        stopWatch.stop();

        log.info("Total result(3Sec): {}", stopWatch.getTotalTimeMillis());
        log.info("---- restTemplateConnect over!");
    }

}
