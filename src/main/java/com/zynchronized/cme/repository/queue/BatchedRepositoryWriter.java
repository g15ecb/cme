package com.zynchronized.cme.repository.queue;

import com.zynchronized.cme.repository.PalindromeRepository;
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

  @Autowired PalindromeRepository repository;

  Result process(final RepositoryWriteQueue q) {
    return q.dequeue();
  }

  boolean persist(final Result r) {
    boolean persisted = false;
    if (!repository.contains(r.input())) {
      repository.put(r);
      persisted = true;
    }
    return persisted;
  }

  @Override
  public void run() {
    log.info("Starting. Poll-Backoff(ms): {}", backoff);
    while (true) {
      try {
        final Result r = process(RepositoryQueue.Q);
        if (null == r) {
          Thread.sleep(backoff);
          continue;
        }
        final boolean persisted = persist(r);
        log.trace("Persisted({}): {}", persisted, r);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
