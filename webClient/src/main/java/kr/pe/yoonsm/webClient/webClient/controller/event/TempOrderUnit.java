package kr.pe.yoonsm.webClient.webClient.controller.event;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-30
 */
@Slf4j
public class TempOrderUnit implements Runnable, OrderChangeEvent {

    boolean runFlg = true;

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(10);
            if (runFlg) log.info("실행되었음 : {}", runFlg);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(String orderNo) {
        // event가 불리우면 실행됨
        log.info("OrderChangeEvent 실행 : {}", orderNo);
        this.runFlg = false;
    }
}
