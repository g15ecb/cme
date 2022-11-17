package com.zynchronized.cme.repository.queue;

public interface RepositoryWriteQueue {

  void enqueue(final String s);

  String dequeue();
}
