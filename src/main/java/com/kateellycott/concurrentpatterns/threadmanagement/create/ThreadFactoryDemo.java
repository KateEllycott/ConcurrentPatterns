package com.kateellycott.concurrentpatterns.threadmanagement.create;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

class MyThreadFactory implements ThreadFactory {
    private int counter;
    private final String name;
    private List<String> stats;

    public MyThreadFactory(String name) {
        counter = 0;
        this.name = name;
        stats = new ArrayList<>();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, name + "_Thread_counter: " + counter);
        counter++;
        stats.add(String.format("Created thread %d with name %s on date: %s\n",
                thread.getId(), thread.getName(), new Date()));
        return thread;
    }

    public String getStats() {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<String> it = stats.iterator();

        while (it.hasNext()) {
            stringBuffer.append(it.next());
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }
}

class Task implements Runnable {
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class ThreadFactoryDemo {
    public static void main(String[] args) {
        MyThreadFactory threadFactory = new MyThreadFactory("MyThreadFactory");
        Task task = new Task();

        System.out.printf("Starting the threads: \n");
        Thread thread;
        for(int i = 0; i < 10; i++) {
            thread = threadFactory.newThread(task);
            thread.start();
        }

        System.out.println("Factory Stats: ");
        System.out.println(threadFactory.getStats());
    }
}
