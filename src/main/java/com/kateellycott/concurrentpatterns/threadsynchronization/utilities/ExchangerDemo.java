package com.kateellycott.concurrentpatterns.threadsynchronization.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Exchanger;

class Producer implements Runnable {
    private Exchanger<List<String>> exchanger;
    private List<String> buffer;

    Producer(Exchanger<List<String>> exchanger, List<String> buffer) {
        this.exchanger = exchanger;
        this.buffer = buffer;
    }
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.printf("Producer is filling the buffer: %s\n", new Date());
                for (int j = 0; j < 10; j++) {
                    buffer.add(String.valueOf(i + j));
                }
                System.out.printf("Producer has filled the buffer %s\n", new Date());
                buffer = exchanger.exchange(buffer);
                System.out.printf("Producer: Buffer's size after an exchange: %d\n", buffer.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {

    private Exchanger<List<String>> exchanger;
    private List<String> buffer;

    Consumer(Exchanger<List<String>> exchanger, List<String> buffer) {
        this.exchanger = exchanger;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.printf("Consumer is waiting for the buffer %s\n", new Date());
                buffer = exchanger.exchange(buffer);
                System.out.printf("Consumer has got message from the Producer: %s\n", new Date());
                for (String value: buffer) {
                    System.out.printf(value);
                }
                buffer.clear();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class ExchangerDemo {
    public static void main(String[] args) {
        Exchanger<List<String>> exchanger = new Exchanger<>();
        List<String> producerBuffer = new ArrayList<>();
        List<String> consumerBuffer = new ArrayList<>();

        Producer producer = new Producer(exchanger, producerBuffer);
        Consumer consumer = new Consumer(exchanger, consumerBuffer);

        new Thread(producer).start();
        new Thread(consumer).start();
    }
}
