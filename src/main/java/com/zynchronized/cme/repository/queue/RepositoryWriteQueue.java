package com.zynchronized.cme.repository.queue;

public interface RepositoryWriteQueue {

  /**
   * Enables enqueues. This is a little hack my side to quickly prevent the bootstrapping of the
   * in-memory cache from generating persistence requests.
   */
  void enableEnqueue();

  void enqueue(final Result r);

  Result dequeue();
}
