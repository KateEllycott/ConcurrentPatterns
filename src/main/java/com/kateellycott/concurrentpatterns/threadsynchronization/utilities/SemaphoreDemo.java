package com.kateellycott.concurrentpatterns.threadsynchronization.utilities;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class PrintQueue {
    private final Semaphore semaphore;
    private final boolean[] freePrinters;
    private final Lock lockPrinters;

    PrintQueue() {
        semaphore = new Semaphore(3);
        freePrinters = new boolean[3];
        for(int i = 0; i < 3; i++) {
            freePrinters[i] = true;
        }
        lockPrinters = new ReentrantLock();
    }

    public void printJob(Object document) {
        try {
            semaphore.acquire();
            int assignedPrinters  = getPrinter();
            long duration = (long)(Math.random()*10);
            System.out.printf("%s - %s: PrintQueue: Printing o Job in Printer %d during %d seconds\n",
                    new Date(), Thread.currentThread().getName(), assignedPrinters, duration);
            TimeUnit.SECONDS.sleep(duration);
            System.out.printf("%s The document has been printed\n", Thread.currentThread().getName());
            freePrinters[assignedPrinters] = true;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            semaphore.release();

        }
    }

    private int getPrinter() {
        int ret = -1;
        try {
            lockPrinters.lock();
            for(int i = 0; i < freePrinters.length; i++) {
                if(freePrinters[i]) {
                    ret = i;
                    freePrinters[i] = false;
                    break;
                }
            }
        }
        finally {
            lockPrinters.unlock();
        }
        return ret;
    }
}

class Job implements Runnable {

    private PrintQueue printQueue;

    Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    @Override
    public void run() {
        System.out.printf("%s Going to print a Job\n", Thread.currentThread().getName());
        printQueue.printJob(new Object());
    }
}
public class SemaphoreDemo {

    public static void main(String[] args) {
        PrintQueue printQueue = new PrintQueue();
        Job job = new Job(printQueue);
        Thread[] threads = new Thread[12];

        for(int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(job);
        }

        for(int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
    }
}
