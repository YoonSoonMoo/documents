package kr.pe.yoonsm.ehcache.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static kr.pe.yoonsm.ehcache.config.EhcacheConfig.CACHE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheReplicationService {

    @Value("${replication.value}")
    private Integer replicationValue;

    final MemoryRepository memoryRepository;


    @CachePut(cacheNames = CACHE_NAME)
    public Integer getReplicationValue() {
        return ++replicationValue;
    }

    @Cacheable(cacheNames = CACHE_NAME)
    public int getCachedReplicationValue() {
        return replicationValue;
    }

    @CachePut(cacheNames = CACHE_NAME)
    public void setCallInfo(CallMappingDto callMappingDto){
        memoryRepository.save(callMappingDto);
        log.info("inputInfo : {}",callMappingDto.toString());
    }

    @Cacheable(cacheNames = CACHE_NAME)
    public CallMappingDto getCallInfo(){
        return memoryRepository.getCallMapping();
    }
}
