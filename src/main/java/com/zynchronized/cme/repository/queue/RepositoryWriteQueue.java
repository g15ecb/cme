package com.zynchronized.cme.repository.queue;

public interface RepositoryWriteQueue {

  void enqueue(final Result r);

  Result dequeue();
}
