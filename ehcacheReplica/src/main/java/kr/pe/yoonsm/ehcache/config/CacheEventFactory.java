package kr.pe.yoonsm.ehcache.config;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

import java.util.Properties;

/**
 * Created by yoonsm@daou.co.kr on 2023-03-31
 */
@Slf4j
public class CacheEventFactory extends CacheEventListenerFactory {

    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return new CacheEventHandler();
    }
}
