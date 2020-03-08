package com.kateellycott.concurrentpatterns.threadmanagement.interrupting;

public class PrimeCalculator extends Thread {
    @Override
    public void run() {
        long number = 1l;

        while(true) {
            if(isPrime(number)) {
                System.out.printf("Number %d is Prime\n", number);
            }
            if(isInterrupted()) {
                System.out.printf("Prime Generator has been interrupted!\n");
                return;
            }
            number++;
        }
    }

    private boolean isPrime(long number) {
        if(number <= 2) {
            return true;
        }
        for(int i = 2; i < number; i++) {
            if(number % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        PrimeCalculator primeCalculator = new PrimeCalculator();
        primeCalculator.start();

        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        primeCalculator.interrupt();



        System.out.printf("Main: Status of the thread %s\n", primeCalculator.getState());
        System.out.printf("Main: isInterrupted: %s\n", primeCalculator.isInterrupted());
        System.out.printf("Main: isAlive: %s\n", primeCalculator.isAlive());

    }
}
