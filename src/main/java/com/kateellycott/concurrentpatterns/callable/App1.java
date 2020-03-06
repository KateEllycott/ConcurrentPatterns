package com.kateellycott.concurrentpatterns.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class App1 {
    public static void main(String[] args) throws Exception {
        Callable<String> callableTask = ()-> "Hello";
        Future<String> futureResult = Executors.newSingleThreadExecutor().submit(callableTask);
        System.out.println(futureResult.get());
        futureResult.cancel(false);
    }
}

