package com.kateellycott.concurrentpatterns.barrier;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

class Friend implements Callable<String> {
    private CyclicBarrier barrier;

    public Friend(CyclicBarrier barrier) {
        this.barrier = barrier;
    }
    @Override
    public String call() throws Exception {
       try {
            Random random = new Random();
            Thread.sleep(random.nextInt(20) * 100 + 100);
            System.out.println("I am just arrived, waiting for the others...");
            barrier.await();
            System.out.println("Let's go to the cinema!");
            return "ok";
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
           return  "not ok";
        }
    }
}

public class BarrierInAction {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(4, () -> System.out.println("Barrier opening..."));
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<String>> futures = new ArrayList<>();
            try {

        for (int i = 0; i < 4; i++) {
            futures.add(executorService.submit(new Friend(cyclicBarrier)));
        }
        futures.forEach(future -> {
            try {
                future.get(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
            }
            catch (TimeoutException e) {
                System.out.println("Time Out");
                future.cancel(true);
            }
        });
    } finally {
                executorService.shutdown();
            }
    }

}
