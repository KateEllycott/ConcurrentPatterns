package com.kateellycott.concurrentpatterns.forkjoinframework;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

class ArrayGenerator {

    private int[] numbers;

    public int[] generateArray(int size) {
        numbers = new int[size];
        Random  random = new Random();
        for(int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(10);
        }
        return numbers;
    }
}

class TaskManager {

    private final ConcurrentLinkedDeque<SearchNumberTask> tasks;

    TaskManager() {
        tasks = new ConcurrentLinkedDeque<>();
    }

    public void addTask(SearchNumberTask task) {
        tasks.add(task);
    }

    public void cancelTasks(SearchNumberTask cancelTask) {
        for(SearchNumberTask task: tasks) {
            if(task != cancelTask) {
                task.cancel(true);
                task.logCancelMessage();
            }
        }
    }
}

class SearchNumberTask extends RecursiveTask<Integer> {
    public static final int NOT_FOUND = -1;

    private TaskManager manager;
    private int[] numbers;
    private int start;
    private int end;
    private int number;

    SearchNumberTask(TaskManager manager, int[] numbers, int start,int end, int number) {
        this.manager = manager;
        this.numbers = numbers;
        this.start = start;
        this.end = end;
        this.number = number;
    }
    @Override
    protected Integer compute() {
        int result = NOT_FOUND;
        System.out.printf("Start: %d, end: %d\n", start, end);
        if(end - start > 10) {
            result = launchTasks();
        }
        else {
            result = lookForNumber();

        }
        return result;
    }

    private int launchTasks() {
        int middle = (start + end) / 2;
        SearchNumberTask task1 = new SearchNumberTask(manager, numbers, start, middle, number);
        SearchNumberTask task2 = new SearchNumberTask(manager, numbers, middle, end, number);

        manager.addTask(task1);
        manager.addTask(task2);

        task1.fork();
        task2.fork();

        int result = task1.join();
        if(result != NOT_FOUND) {
            return result;
        }
        result = task2.join();
        return  result;



    }

    private int lookForNumber() {
        for(int i = 0; i < numbers.length; i++) {
            if(numbers[i] == number) {
                System.out.printf("Number: %d founded in the position %d\n", number, i);
                manager.cancelTasks(this);
                return i;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return NOT_FOUND;
    }

    public void logCancelMessage() {
        System.out.printf("Task: canceled task from start: %d to end: %d\n", start, end);
    }

}

public class CancelForkJoinTasksDemo {
    public static void main(String[] args) {
        ArrayGenerator generator = new ArrayGenerator();
        int[] numbers = generator.generateArray(10_000);
        TaskManager manager = new TaskManager();
        SearchNumberTask task = new SearchNumberTask(manager, numbers, 0, numbers.length, 7);
        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);
        pool.shutdown();

        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: the program has finished\n");
    }
}
