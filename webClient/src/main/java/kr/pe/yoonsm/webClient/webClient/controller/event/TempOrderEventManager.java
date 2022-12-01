package kr.pe.yoonsm.webClient.webClient.controller.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-30
 */
@Component
@Slf4j
public class TempOrderEventManager {
    Map<String, TempOrderUnit> eventList = new HashMap<>();

    public TempOrderUnit createTempOrderUnit(String orderNo) {
        TempOrderUnit tempOrderUnit = new TempOrderUnit(orderNo);
        eventList.put(orderNo, tempOrderUnit);
        log.info("이벤트 리스트에 추가 {}",orderNo);
        return tempOrderUnit;
    }

    @EventListener(OrderChangeEvent.class)
    public void orderChange(OrderChangeEvent orderChangeEvent) {
        TempOrderUnit tempOrderUnit = eventList.get(orderChangeEvent.getOrderNo());
        if (tempOrderUnit != null) {
            tempOrderUnit.expired();
            // 이벤트 관리대상에 제외
            eventList.remove(orderChangeEvent.getOrderNo());
            log.info("이벤트 관리대상 리스트에서 제거 {}",orderChangeEvent.getOrderNo());
        }
    }
}
