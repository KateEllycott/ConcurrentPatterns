package com.kateellycott.concurrentpatterns.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayingWithExecutorsAndRunnables {
    public static void main(String[] args) {
        Runnable runnableTask = () -> System.out.println("I am in a thread named: " + Thread.currentThread().getName());
        // 2 ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            //1 new Thread(runnableTask).start();
            executorService.execute(runnableTask);


        }
        executorService.shutdown();
    }
}
