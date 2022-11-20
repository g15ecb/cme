package com.zynchronized.cme.repository;

import com.zynchronized.cme.repository.queue.Result;
import java.util.List;

public interface PalindromeRepository {

  /**
   * Puts the results into the repository.
   *
   * @param results
   */
  void put(List<Result> results);

  /** Gets an iterator for the repository. */
  PalindromeRepositoryIterator iterator();
}
