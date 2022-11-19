package com.zynchronized.cme.repository;

import com.zynchronized.cme.repository.queue.Result;
import java.util.List;

public interface PalindromeRepository {

  void put(List<Result> results);

  PalindromeRepositoryIterator iterator();
}
