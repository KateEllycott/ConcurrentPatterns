package com.kateellycott.concurrentpatterns.threadexecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

class FactorialCalculator implements Callable<Integer> {

    private final Integer number;

    FactorialCalculator(Integer number) {
        this.number = number;
    }
    @Override
    public Integer call() throws Exception {
        int result = 1;
        if(number > 1) {
            for (int i = 2; i < number; i++) {
                result *= i;
                TimeUnit.MILLISECONDS.sleep(20);
            }
        }
        System.out.printf("%s: %d\n", Thread.currentThread().getName(), result);
        return result;
    }
}

public class CallableDemo {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        List<Future<Integer>> futures = new ArrayList<>();
        Random random = new Random();

        for(int i = 0; i < 10; i++) {
            int number = random.nextInt(10);
            FactorialCalculator factorialCalculator = new FactorialCalculator(number);
            futures.add(executor.submit(factorialCalculator));
        }

        do {
            System.out.printf("Main: number of completed tasks: %d\n", executor.getCompletedTaskCount());
            for(int i = 0; i < futures.size(); i++) {
                Future<Integer> result = futures.get(i);
                System.out.printf("Main: task #%d is done: %s \n", i, result.isDone());

                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while(executor.getCompletedTaskCount() < futures.size());

        System.out.printf("Main: results: \n");
        for(int i = 0; i < futures.size(); i++) {
            Future<Integer> result = futures.get(i);
            Integer number = null;

            try {
                number = result.get();
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.printf("Main: Task #%d , result: %d\n", i, number);
        }
        executor.shutdown();
    }
}
