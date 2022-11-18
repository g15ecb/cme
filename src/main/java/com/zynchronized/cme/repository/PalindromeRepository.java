package com.zynchronized.cme.repository;

import com.zynchronized.cme.repository.queue.Result;

public interface PalindromeRepository {

  void put(Result r);

  boolean contains(String k);

  PalindromeRepositoryIterator iterator();
}
