package com.zynchronized.cme.cache.ehcache;

import com.zynchronized.cme.repository.queue.RepositoryQueue;
import com.zynchronized.cme.repository.queue.RepositoryWriteQueue;
import com.zynchronized.cme.repository.queue.Result;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;

public class EhcacheEventListener implements CacheEventListener<String, Boolean> {

  boolean handle(
      final CacheEvent<? extends String, ? extends Boolean> cacheEvent,
      final RepositoryWriteQueue q) {
    boolean processed = false;
    if (EventType.CREATED == cacheEvent.getType()) {
      q.enqueue(new Result(cacheEvent.getKey(), cacheEvent.getNewValue()));
      processed = true;
    }
    return processed;
  }

  @Override
  public void onEvent(final CacheEvent<? extends String, ? extends Boolean> cacheEvent) {
    handle(cacheEvent, RepositoryQueue.Q);
  }
}
