package com.kateellycott.concurrentpatterns.streams.reactive;

import java.sql.SQLOutput;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;

public class MyReactiveApp {
    public static void main(String[] args) throws InterruptedException {

        SubmissionPublisher<Employee> publisher = new SubmissionPublisher<>();
        MySubscriber subscriber = new MySubscriber();
        publisher.subscribe(subscriber);

        List<Employee> employees = EmployeeListGenerator.generate(1000);

        System.out.println("Publishing Items on Subscriber");
        employees.stream().forEach(employee -> {publisher.submit(employee);
            System.out.println("Submitted: " + employee);});
        System.out.println("Before while loop");
        while(employees.size() != subscriber.getCounter()) {
            Thread.sleep(10);
            System.out.println("After wait");
        }

        publisher.close();

        System.out.println("Exiting the app");

    }
}
