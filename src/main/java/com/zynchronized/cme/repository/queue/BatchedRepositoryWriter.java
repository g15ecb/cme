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
        final String s = RepositoryQueue.Q.dequeue();
        if (null == s) {
          Thread.sleep(1000);
          continue;
        }
        if (repository.contains(s)) {
          log.debug("Not writing, {}", s);
        } else {
          log.debug("Writing, {}", s);
          repository.put(s, false);
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
