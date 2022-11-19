package com.zynchronized.cme.repository.rocksdb;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zynchronized.cme.CmeApplication;
import com.zynchronized.cme.RocksDbUtil;
import com.zynchronized.cme.repository.queue.Result;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CmeApplication.class)
@TestPropertySource(
    properties = {
      "rocksdb.databaseFile=rocks.db",
      "batchedrepositorywriter.backoff=1000",
      "batchedrepositorywriter.batchSize=1000"
    })
class RocksDbRepositoryTest {

  private static final Logger log = LoggerFactory.getLogger(RocksDbRepository.class);

  @Autowired private RocksDbRepository repository;

  @BeforeEach
  public void before() {
    RocksDbUtil.removeDb("rocks.db");
  }

  @AfterEach
  public void after() {
    RocksDbUtil.removeDb("rocks.db");
  }

  private Set<String> write(final int size) {
    final var results =
        Stream.generate(() -> new Result(UUID.randomUUID().toString(), false))
            .limit(size)
            .collect(Collectors.toList());
    final Instant start = Instant.now();
    repository.put(results);
    log.info(
        "Batch[size={}]; Took(ms): {}", size, Duration.between(start, Instant.now()).toMillis());
    return results.stream().map(Result::input).collect(Collectors.toSet());
  }

  boolean allKeysRead(final Set<String> keys) throws Exception {
    try (var iter = repository.iterator()) {
      while (iter.hasNext()) {
        keys.remove(iter.next().input());
      }
    }
    return keys.isEmpty();
  }

  // I haven't gone to the trouble to do in-depth performance analysis but on my basic setup the
  // difference between writing a small batch of items vs. individual puts on that same quantity is
  // ~2-3x faster in favour of the batch variant.
  // Hardware: M1 mac mini, 8GB memory; Software: OSX Ventura 13.0.1, Corretto 17.0.5

  @Test
  public void testBatchPut() throws Exception {
    final int batchSize = 1_000;
    final var keysWritten = write(batchSize);
    assertTrue(allKeysRead(keysWritten));
  }
}
