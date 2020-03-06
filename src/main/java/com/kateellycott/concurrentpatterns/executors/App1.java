package com.kateellycott.concurrentpatterns.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class App1 {
    public static void main(String[] args) {
        Runnable runnable = () -> System.out.println("Hello");
        Thread task = new Thread(runnable);
        task.start();
        Callable<String> callableTask = ()-> "Hello";
        Future<String> futureResult = Executors.newSingleThreadExecutor().submit(callableTask);
    }
}
