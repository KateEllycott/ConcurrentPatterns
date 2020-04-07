package com.kateellycott.concurrentpatterns.threadsynchronization.utilities;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

class MyPhaser extends Phaser {

    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        switch(phase) {
            case 0:
                System.out.printf("All the students are ready to do the exam\n");
                return studentsArrived();
            case 1:
                System.out.printf("All the students has finished the first task\n");
                return firstTaskFinished();
            case 2:
                System.out.printf("All the students has finished the second task\n");
                return secondTaskFinished();
            case 3:
                System.out.printf("All the students has finished the third task\n");
                return thirdTaskFinished();
            default:
                System.out.printf("The exam is over!\n");
                return true;
        }
    }

    private boolean studentsArrived() {
        System.out.printf("Phaser: All student has arrived, we have %d students\n",
                getRegisteredParties());
        return false;
    }

    private boolean firstTaskFinished() {
        System.out.printf("Phaser: All the students has finished the first exercise\n");
        System.out.printf("Phaser: Let's begin the second one!\n");
        return false;
    }

    private boolean secondTaskFinished() {
        System.out.printf("Phaser: All the students has finished the second exercise\n");
        System.out.printf("Phaser: Let's begin the third one!\n");
        return false;
    }

    private boolean thirdTaskFinished() {
        System.out.printf("Phaser: All the students has finished the third exercise\n");
        System.out.printf("Phaser: Let's go home!\n");
        return true;
    }
}

class Student implements Runnable {

    private Phaser phaser;
    Student(Phaser phaser) {
        this.phaser = phaser;
    }

    @Override
    public void run() {
        try {
            System.out.printf("Student: %s has arrived to do the exam: %s\n",
                    Thread.currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();
            performTask1();
            phaser.arriveAndAwaitAdvance();
            performTask2();
            phaser.arriveAndAwaitAdvance();
            performTask3();
            phaser.arriveAndAwaitAdvance();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void  performTask1()throws InterruptedException {
        System.out.printf("Student: %s is performing task1\n", Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(5);
    }

    void  performTask2()throws InterruptedException {
        System.out.printf("Student: %s is performing task2\n", Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(5);
    }

    void  performTask3()throws InterruptedException {
        System.out.printf("Student: %s is performing task3\n", Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(5);
    }
}

public class PhaserOnAdvanceDemo {
    public static void main(String[] args) {
        Phaser phaser = new MyPhaser();

        Student[] students = new Student[10];

        for(int i = 0; i < students.length; i++) {
            students[i] = new Student(phaser);
            phaser.register();
        }

        Thread[] threads = new Thread[students.length];

        for(int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(students[i], "Student: " + i);
            threads[i].start();
        }

        try {
            for(Thread thread: threads) {
                thread.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: The Phaser has finished: %s\n", phaser.isTerminated());
    }
}
