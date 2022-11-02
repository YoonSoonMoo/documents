package kr.pe.yoonsm.webClient.webClient.controller;

import kr.pe.yoonsm.webClient.webClient.service.WebClientCompareService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-26
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TestController {

    final WebClientCompareService webClientCompareService;

    /**
     * 3초짜리 처리
     * @return
     */
    @RequestMapping("wait3sec")
    public String waitResponse() {
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        watch.stop();
        return "Process done!! : " + watch.getTotalTimeMillis() + " ms";
    }

    /**
     * non-blocking 처리가 구현된 url
     * @return
     */
    @RequestMapping("webclient")
    public String connectWebClient(){
        StopWatch watch = new StopWatch();
        watch.start();
        webClientCompareService.webClientConnect();
        webClientCompareService.webClientConnect();
        webClientCompareService.webClientConnect();
        watch.stop();
        return "webClinet : "+watch.getTotalTimeSeconds();
    }

    @RequestMapping("resttemplate")
    public String restTemplateClient(){
        StopWatch watch = new StopWatch();
        watch.start();
        webClientCompareService.restTemplateConnect();
        webClientCompareService.restTemplateConnect();
        webClientCompareService.restTemplateConnect();
        watch.stop();
        return "resttemplate : "+watch.getTotalTimeSeconds();
    }
}
