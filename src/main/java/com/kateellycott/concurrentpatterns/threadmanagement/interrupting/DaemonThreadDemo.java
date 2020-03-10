package com.kateellycott.concurrentpatterns.threadmanagement.interrupting;

import java.io.IOException;
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

class Event {
    private Date date;
    private String event;

    Event(Date date, String event) {
        this.date = date;
        this.event = event;
    }

    Date getDate() {
        return date;
    }

    String getEvent() {
        return event;
    }
}

class TaskWriter implements Runnable {
    private final Deque<Event> deque;

    TaskWriter(Deque<Event> deque) {
        this.deque = deque;
    }
    @Override
    public void run() {
        for(int i = 0; i < 100; i++) {
            Event event = new Event(new Date(), String.format("The thread %s has generated an event",
                    Thread.currentThread().getId()));

            deque.addFirst(event);
            System.out.println(event.getEvent() + "added to the deque");
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class CleanerTask extends Thread {
    private final Deque<Event> deque;

    CleanerTask(Deque<Event> deque) {
        this.deque = deque;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            Date date = new Date();
            cleanDeque(date);
        }
    }

    private void cleanDeque(Date date) {
        long difference;
        boolean delete;

        if (deque.size() == 0) {
                return;
        }
        delete = false;

        do {
            Event event = deque.getLast();
                difference = date.getTime() - event.getDate().getTime();
                if (difference > 10000) {
                    System.out.println("Cleaner: " + event.getEvent());
                    deque.removeLast();
                    delete = true;
                }
        } while (difference > 10000);
        if(delete) {
            System.out.println("Cleaner: Size of the queue: " + deque.size());
        }
    }
}

public class DaemonThreadDemo {
    public static void main(String[] args) {
        Deque<Event> deque = new ConcurrentLinkedDeque<>();
        TaskWriter taskWriter = new TaskWriter(deque);
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            new Thread(taskWriter).start();
        }

        CleanerTask cleanerTask = new CleanerTask(deque);
        cleanerTask.start();
    }

}
