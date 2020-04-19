package com.kateellycott.concurrentpatterns.forkjoinframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

class DocumentGenerator {
    private String[] words = { "the", "hello", "goodbye", "java",
                                "thread", "pool", "random"};

    String[][] generateDocument(int numLines, int numWords, String word) {
        String[][] document = new String[numLines][numWords];
        int counter = 0;
        Random random = new Random();
        for(int i = 0; i < numLines; i++) {
            for(int j = 0; j < numWords; j++) {
                int wordIndex = random.nextInt(words.length - 1);
                document[i][j] = words[wordIndex];
                if(document[i][j].equals(word)) {
                    counter++;
                }
            }
        }
        System.out.printf("Word: %s is met %d times\n", word, counter);
        return document;
    }
}

class DocumentTask extends RecursiveTask<Integer> {

    private String[][] document;
    private int start;
    private int end;
    private String word;

    DocumentTask(String[][] document, int start, int end, String word) {
        this.document = document;
        this.start = start;
        this.end = end;
        this.word = word;
    }

    @Override
    protected Integer compute() {
        Integer result = null;
        if((end - start) < 10) {
            result = processLines(document, start, end, word);
        }
        else {
            int middle = (start + end) / 2;
            DocumentTask task1 = new DocumentTask(document, start, middle, word);
            DocumentTask task2 = new DocumentTask(document, middle, end, word);
            invokeAll(task1,task2);
            try {
                result = combineResults(task1.get(), task2.get());
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Integer combineResults(Integer r1, Integer r2) {
        return r1 + r2;
    }

    private Integer processLines(String[][] document, int start, int end, String word) {
        List<LineTask> lineTasks = new ArrayList<>();
        for(int i = start; i < end; i++) {
            LineTask task = new LineTask(document[i], 0, document[i].length, word);
            lineTasks.add(task);
        }
        invokeAll(lineTasks);
        int result = 0;
        for(int i = 0; i < lineTasks.size(); i++) {
            LineTask task = lineTasks.get(i);
            try {
                result = result + task.get();
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

class LineTask extends RecursiveTask<Integer> {

    private String[] document;
    private int start;
    private int end;
    private String word;

    LineTask(String[] document, int start, int end, String word) {
        this.document = document;
        this.start = start;
        this.end = end;
        this.word = word;
    }

    @Override
    protected Integer compute() {

        Integer result = null;
        if ((end - start) < 100) {
            result = count(document, start, end, word);
        }
        else {
            int middle = (start + end) / 2;
            LineTask task1 = new LineTask(document, start, middle, word);
            LineTask task2 = new LineTask(document, middle, end, word);
            invokeAll(task1, task2);
            try {
                result = combineResults(task1.get(), task2.get());
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Integer count(String[] document, int start, int end, String word) {
        int count = 0;
        for(int i = start; i < end; i++) {
            if(document[i].equals(word)) {
                count++;
            }
        }
        return count;
    }

    private Integer combineResults(Integer r1, Integer r2) {
        return r1 + r2;
    }

}

public class RecursiveTaskDemo {
    public static void main(String[] args) {

        DocumentGenerator documentGenerator = new DocumentGenerator();
        String [][] document = documentGenerator.generateDocument(1000, 1000, "the");
        DocumentTask task = new DocumentTask(document, 0, 1000, "the");
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.execute(task);

        do {
            System.out.printf("*****************************************\n");
            System.out.printf("Main: Active Threads: %d\n", commonPool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n", commonPool.getQueuedTaskCount());
            System.out.printf("Main: Steal Count: %d\n", commonPool.getStealCount());
            System.out.printf("******************************************\n");

            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());

        commonPool.shutdown();
        try {
            commonPool.awaitTermination(1, TimeUnit.DAYS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.printf("Main: The word appears %d times in the document\n",task.get());
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
