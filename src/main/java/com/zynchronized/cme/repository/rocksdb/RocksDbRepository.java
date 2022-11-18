package com.zynchronized.cme.repository.rocksdb;

import com.zynchronized.cme.repository.PalindromeRepository;
import com.zynchronized.cme.repository.PalindromeRepositoryIterator;
import com.zynchronized.cme.repository.queue.Result;
import java.io.File;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class RocksDbRepository implements PalindromeRepository {

  private static final Logger log = LoggerFactory.getLogger(RocksDbRepository.class);
  static final byte[] TRUE = new byte[] {1};
  private static final byte[] FALSE = new byte[] {0};

  @Value("${rocksdb.databaseFile}")
  private String databaseFile;

  private File f;
  private RocksDB db;

  @PostConstruct
  void initialise() {
    RocksDB.loadLibrary();
    final var opts = new Options();
    opts.setCreateIfMissing(true);
    log.info("Data file: {}", databaseFile);
    f = new File(databaseFile);
    try {
      db = RocksDB.open(opts, f.getAbsolutePath());
    } catch (RocksDBException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void put(final Result r) {
    log.trace("Persisting, {}", r);
    try {
      db.put(r.input().getBytes(StandardCharsets.UTF_8), r.output() ? TRUE : FALSE);
    } catch (RocksDBException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean contains(final String k) {
    try {
      final var bs = db.get(k.getBytes(StandardCharsets.UTF_8));
      final boolean hit = null != bs && 1 == bs.length;
      return hit;
    } catch (RocksDBException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public PalindromeRepositoryIterator iterator() {
    return new RocksDbIterator(db.newIterator());
  }
}
