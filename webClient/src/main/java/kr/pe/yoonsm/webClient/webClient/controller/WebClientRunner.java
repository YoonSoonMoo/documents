package kr.pe.yoonsm.webClient.webClient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-26
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebClientRunner implements ApplicationRunner {

    private final WebClient.Builder webClientBuilder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        WebClient webClient = webClientBuilder.build();

        StopWatch stopWatch = new StopWatch();
        log.info("start!");
        stopWatch.start();

        Mono<String> resultFor3Sec = webClient.get()
                .uri("http://localhost:8080/wait", String.class)
                .retrieve()
                .bodyToMono(String.class);

        resultFor3Sec.subscribe(result -> {
            log.info("resultFor3Sec: {}", result);
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }

            log.info("result(3Sec): {}", stopWatch.getTotalTimeSeconds());
            stopWatch.start();
        });
    }
}
