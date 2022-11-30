package kr.pe.yoonsm.webClient.webClient.controller.event;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-30
 */
public class MyEvent {

    String eventKey;

    public MyEvent(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getEventKey() {
        return eventKey;
    }
}
