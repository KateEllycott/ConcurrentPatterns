package com.kateellycott.concurrentpatterns.streams.reactive;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class MySubscriber implements Subscriber<Employee> {

    private Flow.Subscription subscription;
    private int counter;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("Subscribed");
        this.subscription = subscription;
        this.subscription.request(1);
        System.out.println("onSubscribe requested 1 item");
    }

    @Override
    public void onNext(Employee item) {
        System.out.println("Processing an employee " + item);
        counter++;
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Some error happened");
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("All processing done");
    }

    public int getCounter() {
        return counter;
    }
}
