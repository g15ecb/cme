package com.zynchronized.cme.cache;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheEventLogger implements CacheEventListener<Object, Object> {

  private static final Logger log = LoggerFactory.getLogger(CacheEventLogger.class);

  @Override
  public void onEvent(CacheEvent<?, ?> cacheEvent) {
    log.info("{}; k={}, v={}", cacheEvent.getType(), cacheEvent.getKey(), cacheEvent.getNewValue());
  }
}
