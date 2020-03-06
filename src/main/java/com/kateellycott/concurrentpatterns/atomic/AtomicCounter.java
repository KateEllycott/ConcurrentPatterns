package com.kateellycott.concurrentpatterns.atomic;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {
    private static AtomicInteger counter = new AtomicInteger(0);
    public static void main(String[] args) {
        class Incrementer implements Runnable {
            @Override
            public void run() {
                for(int i = 0; i < 1_000; i++) {
                    counter.incrementAndGet();
                }
            }
        }

        class Decrementor implements  Runnable {
            @Override
            public void run() {
                for(int i = 0; i < 1_000; i++) {
                    counter.decrementAndGet();
                }
            }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future<?>> futures = new ArrayList<>();

        try {
            for(int i = 0; i < 8; i++) {
                futures.add(executorService.submit(new Incrementer()));
            }
            for(int i = 0; i < 8; i++) {
                futures.add(executorService.submit(new Decrementor()));
            }

            futures.forEach((future -> {
                try {
                    future.get();
                }
                catch (InterruptedException| ExecutionException e) {
                    System.out.println(e.getMessage());
                }
            }));

            System.out.println("Counter: " + counter);
        }
        finally {
            executorService.shutdown();
        }
    }
}
