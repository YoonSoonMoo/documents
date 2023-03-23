package kr.pe.yoonsm.ehcache.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by yoonsm@daou.co.kr on 2023-03-23
 */
@Getter
@Setter
@ToString
public class CallMappingDto implements Serializable {

    private String seq;
    private String callId;
    private String callPhoneNo;
    private String receivePhoneNo;

}
