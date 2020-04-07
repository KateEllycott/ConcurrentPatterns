package com.kateellycott.concurrentpatterns.threadsynchronization;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class FileMock {
    private String[] lines;
    private int index;

    FileMock(int size, int length) {
        lines = new String[size];
        for (int i = 0; i < size; i++) {
            StringBuilder builder = new StringBuilder();
            for(int j = 0; j < length; j++) {
                int randomCharacter = (int)Math.random() * 255;
                builder.append((char)randomCharacter);
            }
            lines[i] = builder.toString();
        }
    }

    boolean hasMoreLines() {
        return index < lines.length;
    }

    String getLine() {
        if(hasMoreLines()) {
            System.out.println("Mock: " + (lines.length - index));
            return lines[index++];
        }
        return null;
    }
}

class Buffer {
    private final LinkedList<String> buffer;
    private final int maxSize;
    private final ReentrantLock lock;
    private final Condition lines;
    private final Condition space;
    private  boolean pendingLines;

    Buffer(int maxSize) {
        buffer = new LinkedList<>();
        this.maxSize = maxSize;
        lock = new ReentrantLock();
        lines = lock.newCondition();
        space = lock.newCondition();
        pendingLines = true;
    }

    void insert(String line) {
        lock.lock();
        try {
            while (buffer.size() == maxSize) {
                space.await();
            }
            buffer.offer(line);
            System.out.printf("Thread: %s inserted line, buffer size: %s\n", Thread.currentThread().getId(), buffer.size());
            lines.signalAll();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    String get() {
        String line = null;
        lock.lock();
            try {
                while (buffer.size() == 0 && (hasPendingLines())) {
                    lines.await();
                }
                if(hasPendingLines()) {
                    line = buffer.poll();
                    System.out.printf("%s: Line Read : %d\n ", Thread.currentThread().getName(), buffer.size());
                    space.signalAll();
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }
            return line;
    }

    synchronized void setPendingLines(boolean pendingLines) {
        this.pendingLines = pendingLines;
    }

    synchronized  boolean hasPendingLines() {
        return pendingLines || buffer.size() > 0;
    }
}

class LineProducer implements Runnable {
    private FileMock mock;
    private Buffer buffer;

    LineProducer(FileMock mock , Buffer buffer) {
        this.mock = mock;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        buffer.setPendingLines(true);
        while (mock.hasMoreLines()) {
            buffer.insert(mock.getLine());
        }
        buffer.setPendingLines(false);
    }
}

class LineConsumer implements Runnable {
    private Buffer buffer;

    LineConsumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (buffer.hasPendingLines()) {
            String line = buffer.get();
            processLine(line);
        }
    }

    private void processLine(String line) {
        try {
            Random random = new Random();
            Thread.sleep(random.nextInt(100));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
public class ConditionsDemo {
    public static void main(String[] args) {
        FileMock mock = new FileMock(100, 10);
        Buffer buffer = new Buffer(20);

        LineProducer producer = new LineProducer(mock, buffer);
        LineConsumer consumer = new LineConsumer(buffer);

        Thread producerTask = new Thread(producer);

        LineConsumer[] consumers = new LineConsumer[3];

        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new LineConsumer(buffer);
        }

        producerTask.start();

        for (int i = 0; i < consumers.length; i++) {
            new Thread(consumers[i]).start();
        }
    }
}
