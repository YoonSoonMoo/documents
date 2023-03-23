package kr.pe.yoonsm.ehcache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * Created by yoonsm@daou.co.kr on 2023-03-23
 */
@Repository
@Slf4j
public class MemoryRepository {

    CallMappingDto callMappingDto = null;

    public void save(CallMappingDto callMappingDto){
        log.info("save callPappingDto");
        this.callMappingDto =callMappingDto;
    }

    public CallMappingDto getCallMapping(){
        log.info("Read from repository : {}" , callMappingDto);
        return callMappingDto;
    }

}
