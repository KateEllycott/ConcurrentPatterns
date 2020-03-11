package com.kateellycott.concurrentpatterns.threadmanagement;

class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.printf("An exception has been cought: \n");
            System.out.printf("In thread %s\n", t.getId());
            System.out.printf("Exception %s: %s\n", e.getClass().getName(), e.getMessage());
            System.out.printf("Stack trace: \n");
            e.printStackTrace(System.out);
            System.out.printf("Thread status: %s", t.getState());
        }
    }

class Task implements Runnable {
    @Override
    public void run() {
        int number = Integer.parseInt("III");
    }
}

public class ProcessUncheckedException {
    public static void main(String[] args) {
        Task task = new Task();
        Thread thread  = new Thread(task);
        thread.setUncaughtExceptionHandler(new ExceptionHandler());
        thread.start();
    }
}

