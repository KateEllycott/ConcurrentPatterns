package com.kateellycott.concurrentpatterns.threadsynchronization;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class PrintQueue {

    private  Lock queueLock;

    PrintQueue(boolean fairMode) {
        queueLock = new ReentrantLock(fairMode);
    }

    void printJob(Object object) {
        queueLock.lock();
        try {
            Long duration = (long)(Math.random()*10000);
            System.out.printf("Thread %s, printJob: duration %d seconds\n", Thread.currentThread().getName(), duration/1000);
            TimeUnit.MILLISECONDS.sleep(duration);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            queueLock.unlock();
        }

        queueLock.lock();
        try {
            Long duration = (long)(Math.random()*10000);
            System.out.printf("Thread %s, printJob: duration %d seconds\n", Thread.currentThread().getName(), duration/1000);
            TimeUnit.MILLISECONDS.sleep(duration);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            queueLock.unlock();
        }
    }
}

class Job implements Runnable {
    private PrintQueue printQueue;

    Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    @Override
    public void run() {
        System.out.printf("Thread: %s - going to print a document\n", Thread.currentThread().getName());
        printQueue.printJob(new Object());
        System.out.printf("Thread: %s - the document has been printed\n", Thread.currentThread().getName());
    }
}
public class ReentrantLockDemo {
    public static void main(String[] args) {
        System.out.printf("Running example with fair mode\n");
        testPrintQueue(true);
        System.out.printf("Running example with unfair mode\n");
        testPrintQueue(false);
    }

    private static void testPrintQueue(boolean fairMode) {
        PrintQueue printQueue = new PrintQueue(fairMode);

        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Job(printQueue), "Thread: " + i);
        }

        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }

        try {
            for (int i = 0; i < 10; i++) {
                threads[i].join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
