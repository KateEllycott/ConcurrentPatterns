package com.kateellycott.concurrentpatterns.streams.reactive;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

public class MyFreelancerSubscriber implements Flow.Subscriber<Freelancer> {

    Subscription subscription;
    private int counter;

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println("Subscribed for Freelancer");
        this.subscription = subscription;
        this.subscription.request(1);
        System.out.println("onSubscribe requested 1 item for Freelancer");
    }

    @Override
    public void onNext(Freelancer item) {
        System.out.println("Processing Freelancer "+item);
        counter++;
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Some error happened in MyFreelancerSubscriber");
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("All Processing Done for MyFreelancerSubscriber");
    }

    public  int getCounter() {
        return counter;
    }
}
