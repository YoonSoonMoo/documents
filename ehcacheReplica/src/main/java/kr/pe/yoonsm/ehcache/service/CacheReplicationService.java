package kr.pe.yoonsm.ehcache.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static kr.pe.yoonsm.ehcache.config.EhcacheConfig.CACHE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheReplicationService {

    @Value("${replication.value}")
    private Integer replicationValue;

    final MemoryRepository memoryRepository;

    final CacheManager cacheManager;

    @CachePut(cacheNames = CACHE_NAME)
    public Integer getReplicationValue() {
        return ++replicationValue;
    }

    @Cacheable(cacheNames = CACHE_NAME)
    public int getCachedReplicationValue() {
        return replicationValue;
    }


    //@CachePut(value = CACHE_NAME ,key = "#callMappingDto.seq")
    public void setCallInfo(CallMappingDto callMappingDto) {
        memoryRepository.save(callMappingDto);
        log.info("cache name : {} ",cacheManager.getCacheNames().stream().findFirst());
        cacheManager.getCache(CACHE_NAME).put(callMappingDto.getSeq(),callMappingDto);
        log.info("inputInfo : {}", callMappingDto.toString());
    }

    //@Cacheable(value = CACHE_NAME , key = "#seq")
    public CallMappingDto getCallInfo(String seq) {
        log.info("-- getCallInfo !!");
        CallMappingDto result =  cacheManager.getCache(CACHE_NAME).get(seq,CallMappingDto.class);
        if(result == null) result = memoryRepository.getData(seq);
        return result;
    }

    //@Cacheable(value = CACHE_NAME)
    public List<CallMappingDto> getAllCallInfo() {
        return memoryRepository.findAll();
    }
    @CacheEvict(value = CACHE_NAME,allEntries = true)
    public void clearCache(){
        log.info("EhCache all clear!!!!!");
    }

    public void clearCache(String seq){
        cacheManager.getCache(CACHE_NAME).evict(seq);
        log.info("EhCache all clear!!!!!");
    }

}
