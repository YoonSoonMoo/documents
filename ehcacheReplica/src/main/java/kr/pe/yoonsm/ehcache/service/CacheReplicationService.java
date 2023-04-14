package kr.pe.yoonsm.ehcache.service;

import kr.pe.yoonsm.ehcache.config.CacheEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

import static kr.pe.yoonsm.ehcache.config.EhcacheConfig.CACHE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheReplicationService {

    final MemoryRepository memoryRepository;

    final CacheManager cacheManager;

    final CallMappingEventHandler callMappingEventHandler;

    final ApplicationEventPublisher applicationEventPublisher;

    /**
     * key = "#callMappingDto.seq" 동작하지 않아 cacheManager로 별도 구현
     * cacheManager의 handling이 더 편함.
     * @param callMappingDto
     */
    //@CachePut(value = CACHE_NAME ,key = "#callMappingDto.seq")
    public void setCallInfo(CallMappingDto callMappingDto) {
        memoryRepository.save(callMappingDto);
        log.info("cache name : {} ", cacheManager.getCacheNames().stream().findFirst());
        cacheManager.getCache(CACHE_NAME).put(callMappingDto.getSeq(), callMappingDto);
        log.info("inputInfo : {}", callMappingDto.toString());
    }

    //@Cacheable(value = CACHE_NAME , key = "#seq")
    public CallMappingDto getCallInfo(String seq) {
        log.info("-- getCallInfo !!");
        CallMappingDto result = cacheManager.getCache(CACHE_NAME).get(seq, CallMappingDto.class);
        if (result == null) result = memoryRepository.getData(seq);
        return result;
    }

    /**
     * 테스트를 위해 MemoryRepository에서 가져오는 것으로 구현
     * @return
     */
    //@Cacheable(value = CACHE_NAME)
    public List<CallMappingDto> getAllCallInfo() {
        return memoryRepository.findAll();
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void clearCache() {
        log.info("EhCache all clear!!!!!");
    }

    public void clearCache(String seq) {
        cacheManager.getCache(CACHE_NAME).evict(seq);

        log.info("EhCache all clear!!!!!");
    }

    public List<CallMappingDto> callOtherService(CallMappingDto callMappingDto) {
        log.info("-- 외부 서비스를 호출했음. 동기로 결과를 수신 !!");
        this.setCallInfo(callMappingDto);

        // 처리가 끝날때까지 대기
        callMappingEventHandler.registCallMapping(callMappingDto);

        return memoryRepository.findAll();
    }

    /**
     * 이벤트 생성
     *
     * @param seq
     */
    public void eventPublish(String seq) {
        log.info("eventPublish seq : {}", seq);
        applicationEventPublisher.publishEvent( new CallMappingEvent(seq, ""));
    }

}
