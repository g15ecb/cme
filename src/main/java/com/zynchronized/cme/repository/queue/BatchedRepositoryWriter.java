package com.zynchronized.cme.repository.queue;

import com.zynchronized.cme.repository.PalindromeRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatchedRepositoryWriter implements RepositoryWriter {

  private static final Logger log = LoggerFactory.getLogger(BatchedRepositoryWriter.class);

  @Value("${batchedrepositorywriter.backoff}")
  private int backoff;

  @Value("${batchedrepositorywriter.batchSize}")
  private int batchSize;

  @Autowired
  PalindromeRepository repository;

  private final AtomicBoolean enabled = new AtomicBoolean();

  Result process(final RepositoryWriteQueue q) {
    return q.dequeue();
  }

  List<Result> batch(final RepositoryWriteQueue q) {
    final List<Result> batch = new ArrayList<>();
    for (int i = 0; i < batchSize - 1; i++) {
      final var r = process(q);
      if (null != r) {
        batch.add(r);
      } else {
        break;
      }
    }
    return batch;
  }

  @Override
  public void run() {
    log.info("Starting. Poll-Backoff(ms): {}; Max-Batch-Size: {}", backoff, batchSize);
    while (true) {
      try {
        Result r = process(RepositoryQueue.Q);
        // a bit messy but we do the check before we allocate the subsequent collection
        if (null == r) {
          Thread.sleep(backoff);
          continue;
        }

        final Instant start = Instant.now();
        final List<Result> thisBatch = batch(RepositoryQueue.Q);
        thisBatch.add(r);
        repository.put(thisBatch);
        log.trace(
            "Wrote: {} result(s), Took(ms): {}",
            thisBatch.size(),
            Duration.between(start, Instant.now()).toMillis());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
