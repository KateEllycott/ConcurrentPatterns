package com.kateellycott.concurrentpatterns.threadmanagement.grouping;

import java.util.Random;

class MyThreadGroup extends ThreadGroup {
    public MyThreadGroup(String name) {
        super(name);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("%s: [exception has been thrown in thread: %s]\n" ,e.getMessage(),  t.getId());
        e.printStackTrace();
        System.out.println("Terminating the rest of the threads..\n");
        interrupt();
    }
}

class Task implements Runnable {
    @Override
    public void run() {
        int result;
        Random random = new Random(Thread.currentThread().getId());

        while(true) {
            result = 1000/((int) (random.nextDouble()*1000000000));

            if(Thread.currentThread().isInterrupted()) {
                System.out.printf("The thread id %s has been interrupted!\n", Thread.currentThread().getId());
                return;
            }
        }
    }
}

public class ThreadGroupDemo {
    public static void main(String[] args) {
        MyThreadGroup myThreadGroup = new MyThreadGroup("My thread group");
        Task task = new Task();
        int numberOfThreads = Runtime.getRuntime().availableProcessors() * 2;
        for(int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(myThreadGroup, task);
            thread.start();
        }
        System.out.printf("Number of threads %d\n", myThreadGroup.activeCount());
        System.out.printf("Information about the thread group");
        myThreadGroup.list();

        Thread[] threads = new Thread[myThreadGroup.activeCount()];
        myThreadGroup.enumerate(threads);

        for(int i = 0; i < threads.length; i++) {
            System.out.printf("Thread: %s, state: %s\n", threads[i].getName(), threads[i].getState());
        }
     }
}
