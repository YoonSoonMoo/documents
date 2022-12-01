package kr.pe.yoonsm.webClient.webClient.controller;

import kr.pe.yoonsm.webClient.webClient.controller.event.TempOrderEventManager;
import kr.pe.yoonsm.webClient.webClient.controller.event.MyEvent;
import kr.pe.yoonsm.webClient.webClient.controller.event.OrderChangeEvent;
import kr.pe.yoonsm.webClient.webClient.service.WebClientCompareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-26
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    final WebClientCompareService webClientCompareService;

    private final ApplicationEventPublisher applicationEventPublisher;


    final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    final TempOrderEventManager eventFactory;

    int orderKey = 0;


    /**
     * 3초짜리 처리
     *
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
     *
     * @return
     */
    @RequestMapping("webclient")
    public String connectWebClient() {
        StopWatch watch = new StopWatch();
        watch.start();
        webClientCompareService.webClientConnect();
        webClientCompareService.webClientConnect();
        webClientCompareService.webClientConnect();
        watch.stop();
        return "webClinet : " + watch.getTotalTimeSeconds();
    }

    @RequestMapping("resttemplate")
    public String restTemplateClient() {
        StopWatch watch = new StopWatch();
        watch.start();
        webClientCompareService.restTemplateConnect();
        webClientCompareService.restTemplateConnect();
        webClientCompareService.restTemplateConnect();
        watch.stop();
        return "resttemplate : " + watch.getTotalTimeSeconds();
    }

    @RequestMapping("triggerEvent")
    public String makeEvent() {
        orderKey++;
        threadPoolTaskExecutor.submit(eventFactory.createTempOrderUnit( Integer.toString(orderKey)));
//        applicationEventPublisher.publishEvent(new MyEvent(Integer.toString(orderKey )));
        return "OK";
    }

    @RequestMapping(value = "/expired/{orderNo}", method = {RequestMethod.GET, RequestMethod.POST})
    public String expiredEvent(@PathVariable("orderNo") String orderNo) {
        applicationEventPublisher.publishEvent(new OrderChangeEvent(orderNo));
        return "OK";
    }

}

