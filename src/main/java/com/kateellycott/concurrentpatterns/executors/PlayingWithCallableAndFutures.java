package com.kateellycott.concurrentpatterns.executors;

import java.util.concurrent.*;

public class PlayingWithCallableAndFutures {
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        Callable<String> callableTask = () -> {
            Thread.sleep(300);
           return  "I am in a thread called " + Thread.currentThread().getName();};
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            for(int i = 0; i < 10; i++) {
                Future<String> future = executorService.submit(callableTask);
                //System.out.println("I got: " + future.get(100, TimeUnit.MICROSECONDS));
                System.out.println("I got: " + future.get());
            }
        } finally {
            executorService.shutdown();
        }

        Callable<String> callableTask1 = () -> { throw  new IllegalStateException("I throw an exception from "+
                Thread.currentThread().getName());};
        ExecutorService executorService1 = Executors.newFixedThreadPool(4);
        try {
            for(int i = 0; i < 10; i++) {
                Future<String> future = executorService1.submit(callableTask1);
                System.out.println("I got: " + future.get());
            }
        } finally {
            executorService1.shutdown();
        }
    }
}
