package kr.pe.yoonsm.history.aop.services;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.springframework.stereotype.Component;

/**
 * Created by yoonsm@daou.co.kr on 2023-03-23
 */
@Slf4j
@Component
public class CacheService implements CacheEventListener<Object,Object> {

    @Override
    public void onEvent(CacheEvent<?, ?> cacheEvent) {
        log.info("cache event logger message. getKey: {} / cacheType: {} / getNewValue:{}", cacheEvent.getKey(), cacheEvent.getType(), cacheEvent.getNewValue());
    }
}
