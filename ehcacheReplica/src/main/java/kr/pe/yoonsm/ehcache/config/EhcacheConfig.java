package kr.pe.yoonsm.ehcache.config;

import lombok.RequiredArgsConstructor;
import net.sf.ehcache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class EhcacheConfig {

    private final Environment env;
    public static final String CACHE_NAME = "replicationCache";

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        ehCacheCacheManager.setCacheManager(cacheManager());
        return ehCacheCacheManager;
    }

    @Bean
    public CacheManager cacheManager() {
        if(env.acceptsProfiles(Profiles.of("local1"))) {
            return CacheManager.create(this.getClass().getResource("/ehcachePeer1.xml"));
        } else if(env.acceptsProfiles(Profiles.of("local2"))) {
            return CacheManager.create(this.getClass().getResource("/ehcachePeer2.xml"));
        }
        return CacheManager.create(this.getClass().getResource("/ehcacheMulticast.xml"));
    }
}
