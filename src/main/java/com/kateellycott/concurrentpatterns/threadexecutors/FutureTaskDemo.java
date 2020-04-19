package com.kateellycott.concurrentpatterns.threadexecutors;

import java.util.concurrent.*;

class ExecutableTask implements Callable<String> {
    private final String name;

    ExecutableTask(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    @Override
    public String call() throws Exception {
            long duration = (long)(Math.random()*10);
            System.out.printf("%s: Waiting %d seconds for results.\n", name, duration);
            TimeUnit.SECONDS.sleep(duration);
        return "Hello, World. I'am " + name;
    }
}

class ResultedTask extends FutureTask<String>  {

    private final String name;

    ResultedTask(ExecutableTask callable) {
        super(callable);
        this.name = callable.getName();
    }

    @Override
    protected void done() {
        if(isCancelled()) {
            System.out.printf("%s: Has been canceled\n", name);
        }
        else {
            System.out.printf("%s: Has finished\n", name);
        }
    }
}

public class FutureTaskDemo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        ResultedTask[] tasks = new ResultedTask[5];

        for(int i = 0; i < tasks.length; i++) {
            tasks[i] = new ResultedTask(new ExecutableTask(("Task : " + i)));
            executor.submit(tasks[i]);
        }

        try {
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < tasks.length; i++) {
            tasks[i].cancel(true);
        }
        try {
            for (int i = 0; i < tasks.length; i++) {
                if(!tasks[i].isCancelled()) {
                    System.out.printf("Result from the task: %s\n", tasks[i].get());
                }
            }
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}
