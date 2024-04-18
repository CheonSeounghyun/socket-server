package com.temp.socketserver.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientThreadPool {

    private static ExecutorService threadPool;
    private static int poolSize;

    public static void init(int threadpoolSize) {
        threadPool = Executors.newFixedThreadPool(threadpoolSize);
        poolSize = threadpoolSize;
    }

    public static ExecutorService getThreadPool() {
        return threadPool;
    }

    public static int getMaxThreadPoolSize() {
        return poolSize;
    }
}
