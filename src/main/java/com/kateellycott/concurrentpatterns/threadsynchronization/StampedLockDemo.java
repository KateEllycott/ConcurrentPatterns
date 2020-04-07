package com.kateellycott.concurrentpatterns.threadsynchronization;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

class Position {

    int x;
    int y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int getX() {
        return x;
    }

    void setX(int x) {
        this.x = x;
    }

    int getY() {
        return y;
    }

    void setY(int y) {
        this.y = y;
    }
}

class PositionWriter implements Runnable {

    private Position position;
    private StampedLock lock;

    PositionWriter(Position position, StampedLock lock) {
        this.position = position;
        this.lock = lock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            long stamp = lock.writeLock();

            try {
                System.out.printf("Writer: lock acquired: %d\n", stamp);
                position.setX(position.getX() + 1);
                position.setY(position.getY() + 1);
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                lock.unlockWrite(stamp);
                System.out.printf("Writer: lock released: %d\n", stamp);
            }
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class PositionReader implements Runnable{

    private Position position;
    private StampedLock lock;

    PositionReader(Position position, StampedLock lock) {
        this.position = position;
        this.lock = lock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            long stamp = lock.readLock();

            try {
                System.out.printf("Reader: %d - (x: %d, y %d)\n", stamp, position.getX(), position.getY());
                TimeUnit.MILLISECONDS.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                lock.unlockRead(stamp);
                System.out.printf("Reader: lock released: %d\n", stamp);
            }

        }
    }
}

class OptimisticPositionReader implements Runnable {
    private Position position;
    private StampedLock lock;

    OptimisticPositionReader(Position position, StampedLock lock) {
        this.position = position;
        this.lock = lock;
    }
    @Override
    public void run() {
        long stump;
        for(int i = 0; i < 100; i++) {
            try {
                stump = lock.tryOptimisticRead();
                int x = position.getX();
                int y = position.getY();
                if(lock.validate(stump)) {
                    System.out.printf("Optimistic reader: %d - (x: %d, y %d)\n",stump, x, y);
                }
                else {
                    System.out.printf("Optimistic reader: %d no free\n",stump, x, y);
                }
                TimeUnit.MILLISECONDS.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

public class StampedLockDemo {

    public static void main(String[] args) {
        Position position = new Position(0,0);
        StampedLock stampedLock = new StampedLock();

        Thread reader = new Thread(new PositionReader(position, stampedLock));
        Thread writer = new Thread(new PositionWriter(position, stampedLock));
        Thread optimisticReader = new Thread(new OptimisticPositionReader(position, stampedLock));

        reader.start();
        writer.start();
        optimisticReader.start();

        try {
            reader.join();
            writer.join();
            optimisticReader.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
