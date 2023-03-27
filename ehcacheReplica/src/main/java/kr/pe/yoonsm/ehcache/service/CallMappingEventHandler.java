package kr.pe.yoonsm.ehcache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yoonsm@daou.co.kr on 2023-03-27
 */
@Component
@Slf4j
public class CallMappingEventHandler {

    private Map<String, CallMappingDto> eventListMap = new HashMap<>();

    @EventListener
    public void process(CallMappingEvent callMappingEvent) {
        eventListMap.remove(callMappingEvent.getSeqNo());
        log.info("매핑이벤트 발생 seqNo : {} ", callMappingEvent.getSeqNo());
    }

    public void registCallMapping(CallMappingDto callMappingDto) {
        eventListMap.put(callMappingDto.getSeq(), callMappingDto);
        boolean exit = true;

        while (exit) {
            try {
                log.info("-- 처리중 --");
                Thread.sleep(1000);
                if (eventListMap.get(callMappingDto.getSeq()) == null) {
                    exit = false;
                    log.info("이벤트 발생 확인!!");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
