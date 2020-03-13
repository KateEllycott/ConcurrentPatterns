package com.kateellycott.concurrentpatterns.threadsynchronization;

import java.util.concurrent.TimeUnit;

class ParkingCash {
    private static final int cost = 2;
    private long cash;

    ParkingCash() {
        cash = 0;
    }

    synchronized void vehiclePay() {
        cash += cost;
    }

    void closeAccount() {
        System.out.println("Close accounting: ");
        long totalAmount = cash;
        synchronized (this) {
            cash = 0;
        }
        System.out.printf("The total amount is: %d", totalAmount);
    }
}

class ParkingStats {
    private long numberOfCars;
    private long numberOfMotorcycles;
    private ParkingCash parkingCash;
    private Object carLock;
    private Object motorcycleLock;

    ParkingStats(ParkingCash parkingCash) {
        numberOfCars = 0;
        numberOfMotorcycles = 0;
        this.parkingCash = parkingCash;
        carLock = new Object();
        motorcycleLock = new Object();
    }

    void carComeIn() {
        synchronized (carLock) {
            numberOfCars++;
        }
    }

    void carGoOut() {
        synchronized (carLock) {
            numberOfCars--;
        }
        parkingCash.vehiclePay();
    }

    void motorcycleComeIn() {
        synchronized (motorcycleLock) {
            numberOfMotorcycles++;
        }
}

    void motorcycleGoOut() {
        synchronized (motorcycleLock) {
            numberOfMotorcycles--;
        }
        parkingCash.vehiclePay();
    }

    long getNumberOfCars() {
        synchronized (carLock) {
            return numberOfCars;
        }
    }

    long getNumberOfMotorcycles() {
        synchronized (motorcycleLock) {
            return numberOfMotorcycles;
        }
    }
}

class Sensor implements Runnable {
    private ParkingStats parkingStats;

    Sensor(ParkingStats parkingStats) {
        this.parkingStats = parkingStats;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            parkingStats.carComeIn();
            parkingStats.carComeIn();

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            parkingStats.motorcycleComeIn();
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            parkingStats.motorcycleGoOut();
            parkingStats.carGoOut();
            parkingStats.carGoOut();
        }

    }
}

public class SynchronizingMethodDemo {
    public static void main(String[] args) {
        ParkingCash parkingCash = new ParkingCash();
        ParkingStats parkingStats = new ParkingStats(parkingCash);

        int availableProcessors = 2 * Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[availableProcessors];

        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(new Sensor(parkingStats));
            thread.start();
            threads[i] = thread;
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Number of cars: %d\n", parkingStats.getNumberOfCars());
        System.out.printf("Number of motorcycles: %d\n", parkingStats.getNumberOfMotorcycles());
        parkingCash.closeAccount();

    }
}
