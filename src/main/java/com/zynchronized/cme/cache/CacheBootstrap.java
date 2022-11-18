package com.zynchronized.cme.cache;

import com.zynchronized.cme.repository.queue.RepositoryWriter;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CacheBootstrap implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger log = LoggerFactory.getLogger(CacheBootstrap.class);

  private final CachingInitialiser cachingProvider;
  private final RepositoryWriter writer;

  public CacheBootstrap(final CachingInitialiser cachingProvider, final RepositoryWriter writer) {
    this.cachingProvider = cachingProvider;
    this.writer = writer;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    final Instant start = Instant.now();
    try {
      cachingProvider.initialise();
      log.info("Took(ms): {}", Duration.between(start, Instant.now()).toMillis());
    } catch (Exception e) {
      log.error("Failed to initialise cache", e);
    }
    new Thread(writer).start();
  }
}
