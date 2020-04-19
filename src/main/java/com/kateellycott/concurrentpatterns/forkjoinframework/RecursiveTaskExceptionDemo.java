package com.kateellycott.concurrentpatterns.forkjoinframework;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

class ArrayTask extends RecursiveTask<Integer> {

    private int[] numbers;
    private int start;
    private int end;

    ArrayTask(int[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        System.out.printf("Task: Start from %d to %d\n", start, end);

        if(end - start < 10) {
                if(3 > start && 3 < end) {
                    throw new RuntimeException("This task throws an Exception:" +
                            " Task from " + start +" to " + end);
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                }
                catch (InterruptedException e) {
                e.printStackTrace();
                }
        }
        else  {
            int middle = (end + start) / 2;
            ArrayTask task1 = new ArrayTask(numbers, start, middle);
            ArrayTask task2 = new ArrayTask(numbers, middle, end);
            invokeAll(task1, task2);
            System.out.printf("Task: Result from %d to %d: %d\n", start, middle, task1.join());
            System.out.printf("Task: Result from %d to %d: %d\n", start, middle, task2.join());

            System.out.printf("Task: End form %d to %d\n", start, end);
        }
        return 0;
    }
}

public class RecursiveTaskExceptionDemo {
    public static void main(String[] args){
        int[] array = new int[100];
        ArrayTask task = new ArrayTask(array, 0, 100);
        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);
        pool.shutdown();

        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(task.isCompletedAbnormally()) {
            System.out.printf("Main: An exception has occurred\n");
            System.out.printf("Main: %s\n", task.getException());
        }
       System.out.printf("Main: Result: %d\n", task.join());
    }
}
