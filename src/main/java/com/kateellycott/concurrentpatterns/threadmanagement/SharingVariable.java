package com.kateellycott.concurrentpatterns.threadmanagement;

import java.util.Date;
import java.util.concurrent.TimeUnit;

class UnsafeTask implements Runnable {
    private Date startDate;
    @Override
    public void run() {
        startDate = new Date();
        System.out.printf("Starting Thread: %s %s\n", Thread.currentThread().getId(), startDate);

        try {
            TimeUnit.SECONDS.sleep((int)Math.rint(Math.random()*10));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Thread Finished: %s %s\n", Thread.currentThread().getId(), startDate);
    }
}

class SafeTask implements Runnable {
    private static ThreadLocal<Date> startDate = new ThreadLocal<>(){
        @Override
        protected Date initialValue() {
            return new Date();
        }
    };

    @Override
    public void run() {
        System.out.printf("Starting Thread: %s %s\n", Thread.currentThread().getId(), startDate.get());
        try {
            TimeUnit.SECONDS.sleep((int)Math.rint(Math.random()*10));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Thread Finished: %s %s\n", Thread.currentThread().getId(), startDate.get());

    }
}

public class SharingVariable {
    public static void main(String[] args) {
        SafeTask safeTask = new SafeTask();
        UnsafeTask unsafeTask = new UnsafeTask();

        for(int i = 0; i < 10; i++) {
            Thread thread = new Thread(safeTask);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(2);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
