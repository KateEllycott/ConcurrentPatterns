package com.kateellycott.concurrentpatterns.threadexecutors;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class DelayTask implements Callable<String> {
    private final String name;

    DelayTask(String name) {
        this.name = name;
    }

    @Override
    public String call() throws Exception {
        System.out.printf("Task: %s Starting at %s\n", name, new Date());
        return "HelloWorld";
    }
}

public class DelayExecutionDemo {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        System.out.printf("Main: Starting at %s\n", new Date());
        for(int i = 0; i < 5; i++) {
            DelayTask task = new DelayTask("Task #" + i);
            executor.schedule(task, i + 1, TimeUnit.SECONDS);
        }
        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: ends on %s\n", new Date());
    }
}
