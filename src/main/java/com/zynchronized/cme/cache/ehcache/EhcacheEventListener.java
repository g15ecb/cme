package com.zynchronized.cme.cache.ehcache;

import com.zynchronized.cme.repository.queue.RepositoryQueue;
import com.zynchronized.cme.repository.queue.Result;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;

public class EhcacheEventListener implements CacheEventListener<String, Boolean> {

  @Override
  public void onEvent(CacheEvent<? extends String, ? extends Boolean> cacheEvent) {
    if (EventType.CREATED == cacheEvent.getType()) {
      RepositoryQueue.Q.enqueue(new Result(cacheEvent.getKey(), cacheEvent.getNewValue()));
    }
  }
}
