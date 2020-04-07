package com.kateellycott.concurrentpatterns.threadsynchronization.utilities;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class VideoConference implements Runnable {
    private CountDownLatch controller;

    VideoConference(int number) {
        controller = new CountDownLatch(number);
    }

    void arrive(String name) {
        System.out.printf("%s has arrived\n", name);
        controller.countDown();
        System.out.printf("VideoConference: waiting for %d participants\n", controller.getCount());
    }


    @Override
    public void run() {
        System.out.printf("VideoConference: Initialization: %d participants\n", controller.getCount());
        try {
            controller.await();
            System.out.printf("VideoConference: All the participants have come.\n");
            System.out.printf("Let's start...\n");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Participant implements Runnable {

    private String name;
    private VideoConference videoConference;

    Participant(String name, VideoConference videoConference) {
        this.name = name;
        this.videoConference = videoConference;
    }
    @Override
    public void run() {
        long duration = (long)(Math.random()*10);
        try {
            TimeUnit.SECONDS.sleep(duration);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        videoConference.arrive(name);
    }
}

public class CountDownLatchDemo {

    public static void main(String[] args) {
        VideoConference videoConference = new VideoConference(10);
        Thread threadConference = new Thread(videoConference);
        threadConference.start();

        Thread[] threads = new Thread[10];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Participant("Participant #" + i, videoConference));
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
     }
}
