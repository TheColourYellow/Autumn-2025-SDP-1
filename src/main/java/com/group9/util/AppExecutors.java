package com.group9.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    // Private constructor prevents instantiation
    private AppExecutors() {
        throw new UnsupportedOperationException("Utility class");
    }
    public static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
}
