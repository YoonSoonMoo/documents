package kr.pe.yoonsm.ehcache.config;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * Created by yoonsm@daou.co.kr on 2023-03-31
 */
@Slf4j
public class CacheEventHandler implements CacheEventListener {

    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        log.info("--- remove");
    }

    @Override
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        log.info("--- put");
    }

    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        log.info("--- update");
    }

    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
        log.info("--- expired");
    }

    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
        log.info("--- Evicted");
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return null;
    }

}
