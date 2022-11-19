package com.zynchronized.cme.cache.ehcache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zynchronized.cme.repository.queue.InMemoryRepositoryQueue;
import com.zynchronized.cme.repository.queue.Result;
import org.ehcache.Cache;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.EventType;
import org.junit.jupiter.api.Test;

class EhcacheEventListenerTest {

  private static final CacheEvent<String, Boolean> createdEvent =
      new CacheEvent<>() {
        @Override
        public EventType getType() {
          return EventType.CREATED;
        }

        @Override
        public String getKey() {
          return "test";
        }

        @Override
        public Boolean getNewValue() {
          return false;
        }

        @Override
        public Boolean getOldValue() {
          return null;
        }

        @Override
        public Cache<String, Boolean> getSource() {
          return null;
        }
      };

  private static final CacheEvent<String, Boolean> otherEvent =
      new CacheEvent<>() {
        @Override
        public EventType getType() {
          return EventType.UPDATED;
        }

        @Override
        public String getKey() {
          return "test";
        }

        @Override
        public Boolean getNewValue() {
          return false;
        }

        @Override
        public Boolean getOldValue() {
          return null;
        }

        @Override
        public Cache<String, Boolean> getSource() {
          return null;
        }
      };

  @Test
  public void testHandleCreated() {
    final var listener = new EhcacheEventListener();
    final var q = new InMemoryRepositoryQueue();
    q.enableEnqueue();
    final boolean actual = listener.handle(createdEvent, q);
    assertTrue(actual);
    final var result = q.dequeue();
    assertEquals(new Result(createdEvent.getKey(), createdEvent.getNewValue()), result);
  }

  @Test
  public void testHandleOtherEvent() {
    final var listener = new EhcacheEventListener();
    final var q = new InMemoryRepositoryQueue();
    q.enableEnqueue();
    final boolean actual = listener.handle(otherEvent, q);
    assertFalse(actual);
    assertNull(q.dequeue());
  }
}
