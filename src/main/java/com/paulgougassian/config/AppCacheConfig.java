package com.paulgougassian.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class AppCacheConfig {
    private final int expireAfterWrite;

    public AppCacheConfig(@Value("${cache.write.expiry}") int expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    @Bean
    public Caffeine<Object, Object> cacheConfig() {
        return Caffeine.newBuilder()
                       .expireAfterWrite(expireAfterWrite, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(caffeine);
        return manager;
    }
}
