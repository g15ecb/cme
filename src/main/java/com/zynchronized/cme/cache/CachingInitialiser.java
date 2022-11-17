package com.zynchronized.cme.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

/** Initialises the underlying cache should initialisation be required. */
public abstract class CachingInitialiser {

  @Autowired protected CacheManager cacheManager;

  /** Initialises any cache state upon application start, if applicable. */
  public abstract void initialise();
}
