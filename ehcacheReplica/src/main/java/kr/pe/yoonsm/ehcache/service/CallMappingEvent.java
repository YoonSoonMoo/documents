package kr.pe.yoonsm.ehcache.service;

import lombok.Getter;

/**
 * Created by yoonsm@daou.co.kr on 2023-03-27
 */
@Getter
public class CallMappingEvent {
    private String seqNo;
    private String callId;

    public CallMappingEvent(String seqNo, String callId) {
        this.seqNo = seqNo;
        this.callId = callId;
    }
}
