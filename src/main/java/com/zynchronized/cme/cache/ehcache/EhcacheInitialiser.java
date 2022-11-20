package com.zynchronized.cme.cache.ehcache;

import com.zynchronized.cme.cache.CachingInitialiser;
import com.zynchronized.cme.repository.PalindromeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

@Component
public class EhcacheInitialiser extends CachingInitialiser {

  private static final Logger log = LoggerFactory.getLogger(EhcacheInitialiser.class);
  private final PalindromeRepository repository;

  public EhcacheInitialiser(final PalindromeRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialise() throws Exception {
    final Cache cache = cacheManager.getCache("palindrome");
    int loaded = 0;
    try (var iter = repository.iterator()) {
      while (iter.hasNext()) {
        final var r = iter.next();
        cache.put(r.input(), r.output());
        ++loaded;
      }
    }
    log.info("Loaded {} persisted cache entries", loaded);
  }
}
