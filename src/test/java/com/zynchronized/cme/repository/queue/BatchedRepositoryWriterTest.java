package com.zynchronized.cme.repository.queue;

import static com.zynchronized.cme.RocksDbUtil.removeDb;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.zynchronized.cme.CmeApplication;
import java.util.Arrays;
import java.util.List;
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
      "rocksdb.databaseFile=test.db",
      "batchedrepositorywriter.backoff=1000",
      "batchedrepositorywriter.batchSize=1000"
    })
class BatchedRepositoryWriterTest {

  private static final String dbFile = "test.db";

  @Autowired private BatchedRepositoryWriter writer;

  @AfterEach
  public void after() {
    removeDb(dbFile);
  }

  @BeforeEach
  public void before() {
    removeDb(dbFile);
  }

  @Test
  public void testProcessEmpty() {
    final InMemoryRepositoryQueue q = new InMemoryRepositoryQueue();
    q.enableEnqueue();
    assertNull(writer.process(q));
  }

  @Test
  public void testProcess() {
    final var q = new InMemoryRepositoryQueue();
    q.enableEnqueue();
    final var expected = new Result("a", false);
    q.enqueue(expected);
    final Result actual = writer.process(q);
    assertEquals(expected, actual);
  }

  @Test
  public void testBatch() {
    final var q = new InMemoryRepositoryQueue();
    q.enableEnqueue();
    final var expected = Arrays.asList(new Result("a", false), new Result("b", true));
    expected.forEach(q::enqueue);
    final List<Result> actual = writer.batch(q);
    assertEquals(expected, actual);
  }
}
