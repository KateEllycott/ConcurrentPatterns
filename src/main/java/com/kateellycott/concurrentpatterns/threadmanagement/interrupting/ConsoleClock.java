package com.kateellycott.concurrentpatterns.threadmanagement.interrupting;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ConsoleClock implements Runnable {
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println("Date: " + new Date());
                TimeUnit.SECONDS.sleep(1);
            }
        }
        catch (InterruptedException e) {
            System.out.println("The thread " + Thread.currentThread().getName() + " has been interrupted");
        }
    }

    public static void main(String[] args) {
        ConsoleClock consoleClock = new ConsoleClock();
        Thread task = new Thread(consoleClock);
        task.start();

        try {
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e) {
            System.out.println("The thread " + Thread.currentThread().getName() + " has been interrupted");
        }

        task.interrupt();

        try {
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e) {
            System.out.println("The thread " + Thread.currentThread().getName() + " has been interrupted");
        }

        System.out.println("Is interrupted: " + task.isInterrupted());
        System.out.println("isAlive: " + task.isAlive());
    }
}
