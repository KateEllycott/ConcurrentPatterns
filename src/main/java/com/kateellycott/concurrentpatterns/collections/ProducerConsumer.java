package com.kateellycott.concurrentpatterns.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ProducerConsumer {

    private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(50);



    public static void main(String[] args) throws InterruptedException {
        class Producer implements Callable<String> {

            public String call() throws Exception {
                int counter = 0;
                while (counter++ < 50) {
                    queue.put(String.valueOf(counter));
                }
                return "Produced: " + (counter - 1);
            }
        }

        class Consumer implements Callable<String> {

            @Override
            public String call() throws Exception {
                int counter = 0;
                while (counter++ < 50) {
                    queue.take();
                }
                return "Consumed: " + (counter - 1);
            }
        }

        List<Callable<String>> producersAndConsumers = new ArrayList<>(4);
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 2; i++) {
            producersAndConsumers.add(new Producer());
        }

        for (int i = 0; i < 2; i++) {
            producersAndConsumers.add(new Consumer());
        }

        try {
            List<Future<String>> futures = executorService.invokeAll(producersAndConsumers);
            futures.forEach((future) -> {
                try {
                    System.out.println(future.get());
                }
                catch (InterruptedException|ExecutionException e) {
                    System.out.println(e.getMessage());
                }
            });
        }
        finally {
            executorService.shutdown();
            System.out.println("Executor is shut down");
        }
    }

}
