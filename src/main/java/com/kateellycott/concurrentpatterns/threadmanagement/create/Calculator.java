package com.kateellycott.concurrentpatterns.threadmanagement.creating;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Calculator implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread " + Thread.currentThread().getName() + " started");
        long current = 1L;
        long max = 20000L;
        long numPrimes = 0L;

        while(current <= max) {
            if(isPrime(current)) {
                numPrimes++;
            }
            current++;
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " ended, number of primes: " + numPrimes);
    }

    private boolean isPrime(long number) {
        if(number <= 2) {
            return true;
        }
        for(long i = 2; i < number; i++) {
            if((number % i) == 0) {
                return false;
            }
        }
        return  true;
    }

    public static void main(String[] args) {
        Thread[] threads = new Thread[10];
        Thread.State[] states = new Thread.State[10];

        for(int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Calculator());
            if((i % 2 ) == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            }
            else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("Thread number #" + i);
        }

        try(BufferedWriter bufferedWriter =
                    Files.newBufferedWriter(Paths.get("data\\log.txt"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        PrintWriter printWriter = new PrintWriter(bufferedWriter) ) {
            for(int i = 0; i < 10; i++) {
                printWriter.println("Main: Status of Thread [name: " + threads[i].getName() + "]" +
                        ": " + threads[i].getState());
                states[i] = threads[i].getState();
            }
            printWriter.println();

            printWriter.flush();

            for(int i = 0; i < 10; i++) {
                threads[i].start();
            }

            boolean finished = false;

            while(!finished) {
                for(int i = 0; i < 10; i++) {
                    if (threads[i].getState() != states[i]) {
                        writeThreadInfo(printWriter, threads[i], states[i]);
                        states[i] = threads[i].getState();
                    }
                }
                finished = true;

                for(int i = 0; i < 10; i++) {
                        finished = finished & (threads[i].getState() == Thread.State.TERMINATED);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeThreadInfo(PrintWriter printWriter, Thread thread, Thread.State state) {
        printWriter.println("Main: Status of Thread [name: " + thread.getName() + "]" +
                ": " + thread.getState());
        printWriter.println("Main: Old Status of Thread : " + state);
        printWriter.println("Main: Priority: " + thread.getPriority());
        printWriter.println("********************************************************");
    }
}


