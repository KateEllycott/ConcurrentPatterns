package com.kateellycott.concurrentpatterns.locks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheWithReadWriteLock {
    Map<Long, String> cache = new HashMap<>();


    public String get(Long key) {
        return cache.get(key);
    }

    public void put (Long key, String value) {
        cache.put(key, value);
    }

    public static void main(String[] args) {
        CacheWithReadWriteLock cacheWithReadWriteLock = new CacheWithReadWriteLock();

        class Producer implements Callable<String> {
            private Random random = new Random();
            ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
            Lock writeLock = readWriteLock.writeLock();
            Lock readLock = readWriteLock.readLock();
            @Override
            public String call() throws Exception {
                while (true) {
                    long key = random.nextInt(1000);
                    try {
                        writeLock.lock();
                        cacheWithReadWriteLock.put(key, Long.toString(key));
                    } finally {
                        writeLock.unlock();
                    }
                    try {
                        readLock.lock();
                        if(cacheWithReadWriteLock.get(key) == null) {
                            System.out.println("Key " + key + " has not been put in the map");
                        } else {
                            System.out.println("It's ok...");
                        }
                    } finally {
                        readLock.unlock();
                    }
                }
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            for (int i = 0; i < 4; i++) {
                executorService.submit(new Producer());
            }
        } finally {
            executorService.shutdown();
        }

    }
}
