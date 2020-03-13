package com.kateellycott.concurrentpatterns.threadsynchronization;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

class EventStorage {

    private int maxSize;
    private Queue<Date> storage;

    EventStorage() {
        maxSize = 10;
        storage = new LinkedList<>();
    }

    synchronized void set() {
        while (storage.size() == maxSize) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        storage.offer(new Date());
        System.out.printf("Set: The Storage Size: %d\n", storage.size());
        notify();
    }

    synchronized void get() {
        while(storage.size() == 0) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        storage.poll();
        System.out.printf("Get: The Storage Size: %d\n", storage.size());
        notify();
    }
}

class Producer implements Runnable {

    private  final EventStorage eventStorage;

    Producer(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    void produce() {
        eventStorage.set();
    }

    @Override
    public void run() {
        for(int i = 0; i < 20; i++) {
            produce();
        }

    }
}

class Consumer implements Runnable {

    private  final EventStorage eventStorage;

    Consumer(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    void consume() {
        eventStorage.get();
}

    @Override
    public void run() {
        for(int i = 0; i < 20; i++) {
            consume();
        }
    }
}

public class WaitNotifyDemo {
    public static void main(String[] args) {
        EventStorage eventStorage = new EventStorage();
        Producer producer = new Producer(eventStorage);
        Consumer consumer = new Consumer(eventStorage);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        consumerThread.start();
        producerThread.start();

    }
}
