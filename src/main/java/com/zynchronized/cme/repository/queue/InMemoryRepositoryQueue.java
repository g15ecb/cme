package com.zynchronized.cme.repository.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Component;

@Component
public class InMemoryRepositoryQueue implements RepositoryWriteQueue {

  private final ConcurrentLinkedQueue<String> q;

  public InMemoryRepositoryQueue() {
    q = new ConcurrentLinkedQueue<>();
  }

  @Override
  public void enqueue(String s) {
    q.add(s);
  }

  @Override
  public String dequeue() {
    return q.poll();
  }
}
