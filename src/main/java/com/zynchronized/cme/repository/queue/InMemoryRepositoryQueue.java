package com.zynchronized.cme.repository.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Component;

@Component
public class InMemoryRepositoryQueue implements RepositoryWriteQueue {

  private final ConcurrentLinkedQueue<Result> q;

  public InMemoryRepositoryQueue() {
    q = new ConcurrentLinkedQueue<>();
  }

  @Override
  public void enqueue(final Result r) {
    q.add(r);
  }

  @Override
  public Result dequeue() {
    return q.poll();
  }
}
