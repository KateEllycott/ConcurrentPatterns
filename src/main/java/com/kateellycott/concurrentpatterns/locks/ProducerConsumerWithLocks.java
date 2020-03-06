package com.kateellycott.concurrentpatterns.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ProducerAndConsumerWithLocks {
    private List<Integer> buffer = new ArrayList<>(50);
    private Lock lock = new ReentrantLock();
    Condition bufferIsFull = lock.newCondition();
    Condition bufferIsEmpty = lock.newCondition();

    boolean isFull(List<Integer> buffer) {
        return (buffer.size() == 50)? true : false;
    }
    boolean isEmpty(List<Integer> buffer) {
        return (buffer.size() == 0) ? true : false;
    }

    class Producer implements Callable<String> {

        @Override
        public String call() throws InterruptedException, TimeoutException{
            int count = 0;

            while (count++ < 50) {
                try {
                    lock.lock();
                    while (isFull(buffer))
                        bufferIsFull.await();
                    buffer.add(1);
                    bufferIsEmpty.signalAll();
                } finally {
                    lock.unlock();
                }
            }
            return "Produced : " + (count - 1);
        }
    }

    class Consumer implements Callable<String> {
        @Override
        public String call() throws Exception {
            int count = 0;

            while (count++ < 50) {

                try {
                    lock.lock();
                    while (isEmpty(buffer))
                        if (!bufferIsEmpty.await(10, TimeUnit.MICROSECONDS)) {
                            throw new TimeoutException("Consumer timeout");
                        }
                    buffer.remove(buffer.size() - 1);
                    bufferIsFull.signalAll();
                } finally {
                    lock.unlock();
                }
            }
            return "Consumed: " + (count - 1);
        }
    }

    public void launchProducersAndConsumers() throws InterruptedException {
        List<Callable<String>> producers = new ArrayList<>();
        List<Callable<String>> consumers = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            producers.add(new Producer());
        }
        for(int i = 0; i < 4; i++) {
            consumers.add(new Consumer());
        }

        List<Callable<String>> producersAndConsumers = new ArrayList<>();
        producersAndConsumers.addAll(producers);
        producersAndConsumers.addAll(consumers);

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
            List<Future<String>> futures = executorService.invokeAll(producersAndConsumers);
            futures.forEach((future) -> {
                try {
                    System.out.println(future.get());

                } catch (InterruptedException| ExecutionException e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            });
        } finally {
            executorService.shutdown();
            System.out.println("Executor service is shut down");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ProducerAndConsumerWithLocks().launchProducersAndConsumers();
    }
}