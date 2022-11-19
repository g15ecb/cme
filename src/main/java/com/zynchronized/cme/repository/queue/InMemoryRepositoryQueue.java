package com.zynchronized.cme.repository.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class InMemoryRepositoryQueue implements RepositoryWriteQueue {

  private final AtomicBoolean enabled;
  private final ConcurrentLinkedQueue<Result> q;

  public InMemoryRepositoryQueue() {
    q = new ConcurrentLinkedQueue<>();
    enabled = new AtomicBoolean();
  }

  @Override
  public void enableEnqueue() {
    enabled.set(true);
  }

  @Override
  public void enqueue(final Result r) {
    if (enabled.get()) {
      q.add(r);
    }
  }

  @Override
  public Result dequeue() {
    return q.poll();
  }
}
