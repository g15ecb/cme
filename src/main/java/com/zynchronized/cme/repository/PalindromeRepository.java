package com.zynchronized.cme.repository;

import java.util.List;

public interface PalindromeRepository {

  void put(String k, boolean v);

  boolean contains(String k);

  List<String> get();
}
