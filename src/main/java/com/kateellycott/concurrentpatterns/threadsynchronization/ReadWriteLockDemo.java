package com.kateellycott.concurrentpatterns.threadsynchronization;

import com.kateellycott.concurrentpatterns.collections.ProducerConsumer;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class PriceInfo {
    private double firstProductPrice;
    private double secondProductPrice;
    private ReentrantReadWriteLock readWriteLock ;
    private ReentrantReadWriteLock.WriteLock writeLock;
    private ReentrantReadWriteLock.ReadLock readLock;

    PriceInfo() {
       readWriteLock = new ReentrantReadWriteLock();
       writeLock = readWriteLock.writeLock();
       readLock = readWriteLock.readLock();
    }

    void setPrices(double firstProductPrice, double secondProductPrice) {
        writeLock.lock();
        System.out.printf("Thread %s: adquired the Write Lock: %s\n", Thread.currentThread().getName(), new Date());
        try {
            TimeUnit.SECONDS.sleep(2);
            this.firstProductPrice = firstProductPrice;
            this.secondProductPrice = secondProductPrice;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            writeLock.unlock();
            System.out.printf("Thread %s: released the Write Lock: %s\n", Thread.currentThread().getName(), new Date());
        }
    }

    double getFirstProductPrice() {
        readLock.lock();
        double value = firstProductPrice;
        readLock.unlock();
        return value;

    }

    double getSecondProductPrice() {
        readLock.lock();
        double value = secondProductPrice;
        readLock.unlock();
        return value;
    }

}

class Reader implements Runnable {
    private final PriceInfo priceInfo;

    Reader(PriceInfo priceInfo) {
        this.priceInfo = priceInfo;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("Thread %s: %s : firstProductPrice info: %f\n",
                    Thread.currentThread().getName(), new Date(), priceInfo.getFirstProductPrice());
            System.out.printf("Thread %s: %s : secondProductPrice info: %f\n",
                    Thread.currentThread().getName(), new Date(), priceInfo.getSecondProductPrice());
        }

    }
}
class Writer implements Runnable {
    private final PriceInfo priceInfo;

    Writer(PriceInfo priceInfo) {
        this.priceInfo = priceInfo;
    }
    @Override
    public void run() {
        for(int i = 0; i < 3; i++) {
            System.out.printf("Writer: Attempt to modify the prices: %s\n", new Date());
            priceInfo.setPrices(Math.random()*10, Math.random()*10);
            System.out.printf("Writer: the prices has been modified: %s\n", new Date());
            try {
                TimeUnit.SECONDS.sleep(2);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


public class ReadWriteLockDemo {
    public static void main(String[] args) {
        PriceInfo priceInfo = new PriceInfo();
        Writer priceWriter = new Writer(priceInfo);
        Runnable[] readers = new Runnable[5];
        Thread[] threads = new Thread[5];

        for(int i = 0; i < readers.length; i++) {
            readers[i] = new Reader(priceInfo);
            threads[i] = new Thread(readers[i]);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        Thread thread = new Thread(priceWriter);
        thread.start();

    }
}
