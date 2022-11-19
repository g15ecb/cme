package com.zynchronized.cme.repository.rocksdb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zynchronized.cme.CmeApplication;
import com.zynchronized.cme.RocksDbUtil;
import com.zynchronized.cme.repository.PalindromeRepository;
import com.zynchronized.cme.repository.queue.Result;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CmeApplication.class)
@TestPropertySource(
    properties = {
      "rocksdb.databaseFile=iter.db",
      "batchedrepositorywriter.backoff=1000",
      "batchedrepositorywriter.batchSize=1000"
    })
class RocksDbIteratorTest {

  private static final String dbFile = "iter.db";

  @Autowired private PalindromeRepository repository;

  @BeforeEach
  public void before() {
    RocksDbUtil.removeDb(dbFile);
  }

  @AfterEach
  public void after() {
    RocksDbUtil.removeDb(dbFile);
  }

  @Test
  public void test() throws Exception {
    final var r1 = new Result("aba", true);
    final var r2 = new Result("happy", false);
    final var expected = Map.of(r1.input(), r1, r2.input(), r2);
    for (final var e : expected.values()) {
      repository.put(Arrays.asList(r1, r2));
    }
    try (var iter = repository.iterator()) {
      final Map<String, Result> actual = new HashMap<>();
      while (iter.hasNext()) {
        final Result r = iter.next();
        actual.put(r.input(), r);
      }
      assertEquals(expected, actual);
    }
  }
}
