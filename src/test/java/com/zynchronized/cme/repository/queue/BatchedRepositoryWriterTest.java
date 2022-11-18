package com.zynchronized.cme.repository.queue;

import static com.zynchronized.cme.RocksDbUtil.removeDb;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zynchronized.cme.CmeApplication;
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
    properties = {"rocksdb.databaseFile=test.db", "batchedrepositorywriter.backoff=1000"})
class BatchedRepositoryWriterTest {

  private static final String dbFile = "test.db";

  @Autowired private RepositoryWriter writer;

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
    final BatchedRepositoryWriter bw = (BatchedRepositoryWriter) writer;
    assertNull(bw.process(new InMemoryRepositoryQueue()));
  }

  @Test
  public void testProcess() {
    final BatchedRepositoryWriter bw = (BatchedRepositoryWriter) writer;
    final var q = new InMemoryRepositoryQueue();
    final var expected = new Result("a", false);
    q.enqueue(expected);
    final Result actual = bw.process(q);
    assertEquals(expected, actual);
  }

  @Test
  public void testPerist() {
    final BatchedRepositoryWriter bw = (BatchedRepositoryWriter) writer;
    assertTrue(bw.persist(new Result("a", true)));
    assertFalse(bw.persist(new Result("a", true)));
  }
}
