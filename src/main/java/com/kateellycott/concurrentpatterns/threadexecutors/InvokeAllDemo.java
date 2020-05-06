package com.kateellycott.concurrentpatterns.threadexecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class Result {

    private String name;
    private int value;

    String getName() {
        return name;
    }

    int getValue() {
        return value;
    }

    void setName(String name) {
        this.name = name;
    }

    void setValue(int value) {
        this.value = value;
    }
}

class ResultTask implements Callable<Result> {

    private final String name;

    ResultTask(String name) {
        this.name = name;
    }

    @Override
    public Result call() throws Exception {
        System.out.printf("Starting: %s\n", this.name);

        try {
            long duration = (long)(Math.random()*10);
            System.out.printf("%s: Waiting results for %d seconds\n", this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        int value = 0;
        for(int i = 0; i < 5; i++) {
            value += (int)(Math.random()*100);
        }
        Result result = new Result();
        result.setName(this.name);
        result.setValue(value);
        return  result;
    }
}

public class InvokeAllDemo {
    public static void main(String[] args) {
        List<Future<Result>> futures = null;
        List<ResultTask> taskList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            taskList.add(new ResultTask("Task #" + i));
        }
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            futures = executor.invokeAll(taskList);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();

        System.out.printf("Main: printing results..\n");
            for(int i =0; i < futures.size(); i++) {
                Future<Result> future = futures.get(i);
                try {
                    Result result = future.get();
                    System.out.println(result.getName() + ": " + result.getValue());
                }
                catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
    }
}
