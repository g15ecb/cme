package com.zynchronized.cme.repository.queue;

public final class RepositoryQueue {

  public static final RepositoryWriteQueue Q = new InMemoryRepositoryQueue();

  private RepositoryQueue() {}
}
