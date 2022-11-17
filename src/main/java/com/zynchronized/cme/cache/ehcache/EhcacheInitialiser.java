package com.zynchronized.cme.cache.ehcache;

import com.zynchronized.cme.cache.CachingInitialiser;
import com.zynchronized.cme.repository.PalindromeRepository;
import com.zynchronized.cme.repository.queue.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

// A number of open source caching libraries lack recoverable persistence layers (they are the paid
// options). Ehcache is a decent caching layer but falls into that category. To keep things simple
// this is an augmented version of ehcache with a leveldb backed persistence store that acts as a
// write-through. LevelDB is lightweight and fast, although its downside is that it's a native
// library
// and so its cost can be boiled down to that of JNI+shipping the resp. platform's library. There
// are
// other options to LevelDB but its basic key-value semantics are ideal, in addition to it being
// reliable
// and fast. Note that this is not a write ahead log (WAL); it's a write-through and I am not
// attempting
// to make the operations of writing to the cache and journal (LevelDB) transactional. I don't think
// the use case here warrants attempting to strive for that stricter semantics. If there were more
// costly
// data being memoized then it would be a different story.
@Component
public class EhcacheInitialiser extends CachingInitialiser {

  private static final Logger log = LoggerFactory.getLogger(EhcacheInitialiser.class);
  private final PalindromeRepository repository;

  public EhcacheInitialiser(final PalindromeRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialise() {
    final Cache cache = cacheManager.getCache("palindrome");
    final var persisted = repository.get();
    for (final Result r : persisted) {
      cache.put(r.getInput(), r.getOutput());
    }
    log.info("Loaded {} persisted cache entries", persisted.size());
  }
}
