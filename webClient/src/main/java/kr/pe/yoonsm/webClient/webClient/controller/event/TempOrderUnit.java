package kr.pe.yoonsm.webClient.webClient.controller.event;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-30
 */
@Slf4j
public class TempOrderUnit implements Runnable {
    boolean runFlg = true;
    String orderKey;

    public TempOrderUnit(String orderKey) {
        this.orderKey = orderKey;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(10);
            if (runFlg) {
               log.info("실행되었음 : {}", orderKey);
            } else {
               log.info("실행되지 않았음: {}" ,orderKey);
            } 

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void expired() {
        // event가 불리우면 실행됨
        log.info("OrderChangeEvent 발생 : {}", orderKey);
        this.runFlg = false;
    }
}
