package com.zynchronized.cme.repository.rocksdb;

import com.zynchronized.cme.repository.PalindromeRepository;
import com.zynchronized.cme.repository.PalindromeRepositoryIterator;
import com.zynchronized.cme.repository.queue.Result;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.PostConstruct;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;
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

  private static byte[] getBoolean(final boolean b) {
    return b ? TRUE : FALSE;
  }

  @Override
  public void put(final List<Result> results) {
    final var batch = new WriteBatch();
    for (final var r : results) {
      try {
        batch.put(r.input().getBytes(StandardCharsets.UTF_8), getBoolean(r.output()));
      } catch (RocksDBException e) {
        log.error("Failed to add result to batch", e);
      }
    }
    try {
      db.write(new WriteOptions(), batch);
    } catch (RocksDBException e) {
      log.error("Failed to write batch", e);
    }
  }

  @Override
  public PalindromeRepositoryIterator iterator() {
    return new RocksDbIterator(db.newIterator());
  }
}
