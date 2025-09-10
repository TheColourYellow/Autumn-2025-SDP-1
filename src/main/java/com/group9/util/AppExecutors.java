package com.group9.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
  public static final ExecutorService databaseExecutor =
          Executors.newSingleThreadExecutor();
}
