package com.zynchronized.cme.repository.rocksdb;

import static com.zynchronized.cme.repository.rocksdb.RocksDbRepository.TRUE;

import com.zynchronized.cme.repository.PalindromeRepositoryIterator;
import com.zynchronized.cme.repository.queue.Result;
import java.util.Arrays;
import java.util.Objects;
import org.rocksdb.RocksIterator;

public final class RocksDbIterator implements PalindromeRepositoryIterator {

  private final RocksIterator iterator;

  public RocksDbIterator(final RocksIterator iterator) {
    this.iterator = Objects.requireNonNull(iterator);
    this.iterator.seekToFirst();
  }

  @Override
  public boolean hasNext() {
    return iterator.isValid();
  }

  @Override
  public Result next() {
    final var r = new Result(new String(iterator.key()), Arrays.equals(iterator.value(), TRUE));
    iterator.next();
    return r;
  }

  @Override
  public void close() throws Exception {
    if (null != iterator && !iterator.isValid()) {
      iterator.close();
    }
  }
}
