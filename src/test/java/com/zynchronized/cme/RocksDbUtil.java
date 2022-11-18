package com.zynchronized.cme;

import java.io.File;
import org.apache.tomcat.util.http.fileupload.FileUtils;

// normally I'd mock this but I actually want to test against the actual database in the cases
// where it's used.
public final class RocksDbUtil {

  private RocksDbUtil() {}

  public static void removeDb(final String dbFile) {
    final var f = new File(dbFile);
    try {
      FileUtils.deleteDirectory(f);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
