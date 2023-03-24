package kr.pe.yoonsm.ehcache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yoonsm@daou.co.kr on 2023-03-23
 */
@Repository
@Slf4j
public class MemoryRepository {

    Map<String,CallMappingDto> mdb = new LinkedHashMap<>();

    public void save(CallMappingDto callMappingDto){
        String key = callMappingDto.getSeq();
        mdb.put(key,callMappingDto);
        log.info("save callPappingDto : {}",callMappingDto);
    }

    public CallMappingDto getData(String seq){
        CallMappingDto result =mdb.get(seq);
        log.info("Repository data return : {}" , result );
        return result;
    }

    public List<CallMappingDto> findAll(){
        log.info("call find all : {} 건" ,mdb.size());
        return  mdb.values().stream().collect(Collectors.toList());
    }
}
