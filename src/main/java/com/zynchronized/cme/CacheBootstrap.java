package com.zynchronized.cme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CacheBootstrap implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger log = LoggerFactory.getLogger(CacheBootstrap.class);
  private final CacheManager cacheManager;

  public CacheBootstrap(final CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    // so the idea is that we read from leveldb which is reliable persistence store, then we push them
    // into our in-memory cache on startup in a diff thread
    final Cache cache = cacheManager.getCache("palindrome");
    cache.put("aba", true);
    log.info("I'm ready!");
  }
}
