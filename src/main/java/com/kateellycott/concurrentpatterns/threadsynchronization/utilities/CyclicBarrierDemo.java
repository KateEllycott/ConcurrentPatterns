package com.kateellycott.concurrentpatterns.threadsynchronization.utilities;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class MatrixMock {

    private final int[][] data;

    MatrixMock(int size, int length, int number) {
        data = new int[size][length];
        Random random = new Random();
        int count = 0;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = random.nextInt(10);
                if(data[i][j] == number) {
                    count++;
                }
            }
        }
        System.out.printf("MatrixMock: There are %d occurrences of the number %d\n", count, number);
    }

    int[] getRow(int index) {
        if(index >= 0 && index < data.length) {
            return data[index];
        }
        return null;
    }
}

class Result {

    private final int data[];

    Result(int size) {
        data = new int[size];
    }

    void setData(int position, int value) {
        data[position] = value;
    }

    int[] getData() {
        return data;
    }
}

class Searcher implements Runnable {

    private final int firstRow;
    private final int lastRow;
    private final MatrixMock matrixMock;
    private final Result results;
    private final int number;
    private final CyclicBarrier barrier;

    Searcher(int firstRow, int lastRow, MatrixMock matrixMock, Result results,
             int number, CyclicBarrier barrier) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.matrixMock = matrixMock;
        this.results = results;
        this.number = number;
        this.barrier = barrier;
    }

    @Override
    public void run() {

        System.out.printf("%s processing lines from %d to %d\n", Thread.currentThread().getName(), firstRow, lastRow);

        for(int i = firstRow; i < lastRow; i++) {
            int counter = 0;
            int[] row = matrixMock.getRow(i);
            for (int j = 0; j < row.length; j++) {
                if(row[j] == number) {
                    counter++;
                }
            }
            results.setData(i, counter);
        }
        System.out.printf("%s: Lines processed\n", Thread.currentThread().getName());
        try {
            barrier.await();
        }
        catch (InterruptedException|BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

class Grouper implements Runnable {
    private final Result result;

    Grouper(Result result) {
        this.result = result;
    }

    @Override
    public void run() {
        int finalResult = 0;
        System.out.printf("Grouper: processing result...\n");
        int[] data = result.getData();
        for(int number: data) {
            finalResult += number;
        }
        System.out.printf("Grouper: final result: %d\n", finalResult);
    }
}
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        final int ROWS = 10000;
        final int NUMBERS  = 1000;
        final int SEARCH = 5;
        final int PARTICIPANTS = 5;
        final int LINES_PARTICIPANTS = 2000;

        MatrixMock matrixMock = new MatrixMock(ROWS, NUMBERS, SEARCH);
        Result results = new Result(ROWS);
        Grouper grouper = new Grouper(results);
        CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, grouper);
        Searcher[] searchers = new Searcher[PARTICIPANTS];

        for(int i = 0; i < searchers.length; i++) {
            searchers[i] = new Searcher(i * LINES_PARTICIPANTS,
                    (i * LINES_PARTICIPANTS) + LINES_PARTICIPANTS, matrixMock, results, SEARCH, barrier);
            Thread thread = new Thread(searchers[i]);
            thread.start();
        }
        System.out.printf("The main thread has finished\n");
    }
}
