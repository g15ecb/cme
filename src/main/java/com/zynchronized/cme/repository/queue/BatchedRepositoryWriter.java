package com.zynchronized.cme.repository.queue;

import com.zynchronized.cme.repository.PalindromeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BatchedRepositoryWriter implements RepositoryWriter {

  private static final Logger log = LoggerFactory.getLogger(BatchedRepositoryWriter.class);

  @Autowired PalindromeRepository repository;

  @Override
  public void run() {
    while (true) {
      try {
        final Result r = RepositoryQueue.Q.dequeue();
        if (null == r) {
          Thread.sleep(1000);
          continue;
        }
        if (repository.contains(r.getInput())) {
          log.debug("Not writing, {}", r);
        } else {
          log.debug("Writing, {}", r);
          repository.put(r);
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
