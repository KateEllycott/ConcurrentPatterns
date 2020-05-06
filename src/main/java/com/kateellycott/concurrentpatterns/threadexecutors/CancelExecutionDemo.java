package com.kateellycott.concurrentpatterns.threadexecutors;

import java.util.concurrent.*;

class CancelledTask implements Callable<String> {

    @Override
    public String call() throws Exception {
        while(true) {
            System.out.printf("Test: Task is running..\n");
                TimeUnit.MILLISECONDS.sleep(100);
        }
    }
}

public class CancelExecutionDemo {
    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        CancelledTask task = new CancelledTask();
        Future<String> future = executor.submit(task);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Cancelling the Task\n");

        System.out.println(future.cancel(true));
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Canceled: %s\n", future.isCancelled());
        System.out.printf("Main: Done: %s\n", future.isDone());
        executor.shutdown();
        System.out.printf("Main: The executor has finished\n");
    }
}
