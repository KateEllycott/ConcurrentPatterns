package com.kateellycott.concurrentpatterns.threadsynchronization.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

class SeedGenerator implements Runnable {

    private CompletableFuture<Integer> resultCommunicator;

    SeedGenerator(CompletableFuture<Integer> resultCommunicator) {
        this.resultCommunicator = resultCommunicator;
    }

    @Override
    public void run() {
        System.out.printf("SeedGenerator: Generating a seed...\n");
        try {
            TimeUnit.SECONDS.wait(5);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        int seed = (int)(Math.random() * 10);
        System.out.printf("SeedGenerator: generated seed: %d\n", seed);
        resultCommunicator.complete(seed);
    }
}

class NumberListGenerator implements Supplier<List<Long>> {

    private final int size;

    NumberListGenerator(int size) {
        this.size = size;
    }
    @Override
    public List<Long> get() {
        System.out.printf("%s: NumberListGenerator: start\n", Thread.currentThread().getName());
        List<Long> ret = new ArrayList<>();

        for(int i = 0; i < size * 1000_000; i++) {
            long number = Math.round(Math.random() * Long.MAX_VALUE);
            ret.add(number);
        }
        System.out.printf("%s: NumberListGenerator: end\n", Thread.currentThread().getName());
        return ret;
    }
}

class NumberSelector implements Function<List<Long>, Long> {
    @Override
    public Long apply(List<Long> longs) {
        System.out.printf("%s Step 3: Start\n", Thread.currentThread().getName());
        long max = longs.stream().max(Long::compare).get();
        long min = longs.stream().min(Long::compare).get();
        long result = (max+min)/2;
        System.out.printf("%s Step 3: Result %d\n", Thread.currentThread().getName(), result);
        return result;
    }
}

public class CompletableFutureDemo {
    public static void main(String[] args) {
        System.out.printf("Main start: \n");
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        SeedGenerator seedGenerator = new SeedGenerator(completableFuture);
        Thread seedThread = new Thread(seedGenerator);
        seedThread.start();

        System.out.printf("Main: getting the seed: \n");
        int seed = 0;

        try {
            seed = completableFuture.get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: the seed is %d\n", seed);

        System.out.printf("Main: launching the list numbers generator\n");
        NumberListGenerator numberListGenerator = new NumberListGenerator(seed);
        CompletableFuture<List<Long>> startFuture = CompletableFuture.supplyAsync(numberListGenerator);

        System.out.printf("Main: launching 1st step\n");
        CompletableFuture<Long> step1Future = startFuture
                .thenApplyAsync(list -> {
                   System.out.printf("%s Step 1: start\n",Thread.currentThread().getName());
                   long selected = 0;
                   long selectedDistance = Long.MAX_VALUE;
                   long distance;
                   for(Long number: list) {
                       distance = Math.abs(number - 1000);
                       if(distance < selectedDistance) {
                           selected = number;
                           selectedDistance = distance;
                       }
                   }
                   System.out.printf("%s Step 1: result %d\n", Thread.currentThread().getName(), selected);
                   return selected;
                });

        System.out.printf("Main: launching 2st step\n");
        CompletableFuture<Long> step2Future = startFuture
                .thenApplyAsync(list -> list.stream().max(Long::compareTo).get());

        CompletableFuture<Void> write2Future = step2Future.
                thenAccept(selected -> System.out.printf("$s Step 2 result: - %d\n", Thread.currentThread().getName(), selected));

        System.out.printf("Main: launching 3st step\n");

        NumberSelector numberSelector = new NumberSelector();
        CompletableFuture<Long> step3Future = startFuture
                .thenApplyAsync(numberSelector);

        CompletableFuture<Void> waitFuture = CompletableFuture
                .allOf(step1Future, step2Future, step3Future);

        CompletableFuture<Void> finalFuture = waitFuture
                .thenAcceptAsync((param) -> {
                    System.out.printf("Main: The completable example has been completed\n");
                });
        finalFuture.join();

    }
}
