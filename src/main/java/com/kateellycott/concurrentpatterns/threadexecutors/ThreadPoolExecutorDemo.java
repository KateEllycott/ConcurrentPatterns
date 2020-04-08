package com.kateellycott.concurrentpatterns.threadexecutors;

import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Task implements Runnable {

    private final Date initDate;
    private final String name;

    Task(Date initDate, String name) {
        this.initDate = initDate;
        this.name = name;
    }

    @Override
    public void run() {
        System.out.printf("%s: Task: %s created on %s\n", Thread.currentThread().getName(), name, initDate);
        System.out.printf("%s: Task: %s started on %s\n", Thread.currentThread().getName(), name, new Date());

        try {
            long duration = (long) (Math.random()*10);
            System.out.printf("%s: Task: %s Doing a task during %d seconds\n", Thread.currentThread().getName(),
                    name, duration);
            TimeUnit.SECONDS.sleep(duration);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Task: %s Finished task on: %s\n", Thread.currentThread().getName(),
                name, new Date());
    }
}

class RejectedTaskController implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.printf("RejectedTaskController: the task %s has been rejected\n", r.toString());
        System.out.printf("RejectedTaskController: %s\n", executor.toString());
        System.out.printf("RejectedTaskController: Executor is terminating: %s\n", executor.isTerminating());
        System.out.printf("RejectedTaskController: Executor is terminated: %s\n", executor.isTerminated());
    }
}

class Server {

    private final ThreadPoolExecutor executor;

    Server() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        RejectedTaskController rejectedTaskController = new RejectedTaskController();
        executor.setRejectedExecutionHandler(rejectedTaskController);
    }

    void executeTask(Runnable task) {
        System.out.printf("Server: A new task has arrived\n");
        executor.execute(task);
        System.out.printf("Server: Pool Size: %d\n", executor.getPoolSize());
        System.out.printf("Server: Active Count: %d\n", executor.getActiveCount());
        System.out.printf("Server: Task Count: %d\n", executor.getTaskCount());
        System.out.printf("Server: Completed Count: %d\n", executor.getCompletedTaskCount());
        System.out.printf("");
    }

    void endServer() {
        executor.shutdown();
    }
}

public class ThreadPoolExecutorDemo {
    public static void main(String[] args) {
        Server server = new Server();

        System.out.printf("Main: Starting..\n");
        for(int i = 0; i < 100; i ++) {
            Task task = new Task(new Date(), "Task " + i);
            server.executeTask(task);
        }
        System.out.printf("Main: Shutting down the executor..\n");
        server.endServer();

        System.out.printf("Main: sending another task to the executor..\n");
        Task rejectedTask = new Task(new Date(), "Rejected Task");
        server.executeTask(rejectedTask);
        System.out.printf("Main: end.");
    }
}
