package kr.pe.yoonsm.webClient.webClient.controller;

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
public class TestController {

    @RequestMapping("wait")
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
}
