package com.zynchronized.cme.repository.rocksdb;

import com.zynchronized.cme.repository.PalindromeRepository;
import com.zynchronized.cme.repository.queue.Result;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RocksDbRepository implements PalindromeRepository {

  private static final Logger log = LoggerFactory.getLogger(RocksDbRepository.class);
  private static final byte[] TRUE = new byte[] {1};
  private static final byte[] FALSE = new byte[] {0};
  private static final String dataFile = "test.db";
  private File f;
  private RocksDB db;

  @PostConstruct
  void initialise() {
    RocksDB.loadLibrary();
    final var opts = new Options();
    opts.setCreateIfMissing(true);
    log.info("Data file: {}", dataFile);
    f = new File(dataFile);
    try {
      db = RocksDB.open(opts, f.getAbsolutePath());
    } catch (RocksDBException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void put(final Result r) {
    log.info("Persisting, {}", r);
    try {
      db.put(r.getInput().getBytes(StandardCharsets.UTF_8), r.getOutput() ? TRUE : FALSE);
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
  public List<Result> get() {
    final var keys = new ArrayList<Result>();
    try (final RocksIterator iter = db.newIterator()) {
      for (iter.seekToFirst(); iter.isValid(); iter.next()) {
        keys.add(new Result(new String(iter.key()), Arrays.equals(iter.value(), TRUE)));
      }
    }
    return keys;
  }
}
