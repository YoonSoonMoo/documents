package kr.pe.yoonsm.webClient.webClient.controller.event;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-30
 */
public interface OrderChangeEvent {
    void update(String orderNo);
}
