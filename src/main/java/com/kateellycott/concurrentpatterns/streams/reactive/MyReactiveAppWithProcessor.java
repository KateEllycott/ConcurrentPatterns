package com.kateellycott.concurrentpatterns.streams.reactive;

import java.util.List;
import java.util.concurrent.SubmissionPublisher;

public class MyReactiveAppWithProcessor {
    public static void main(String[] args) throws InterruptedException {
       List<Employee> employees = EmployeeListGenerator.generate(100);
       SubmissionPublisher<Employee> publisher = new SubmissionPublisher<>();
       MyProcessor processor = new MyProcessor((employee ->
               new Freelancer(employee.getId(), employee.getId() + 10, employee.getName())));
       MyFreelancerSubscriber subscriber = new MyFreelancerSubscriber();

       publisher.subscribe(processor);
       processor.subscribe(subscriber);

       employees.stream().forEach((employee -> {
           publisher.submit(employee);
           System.out.println("Submitted: " + employee);
       }));

        while (employees.size() != subscriber.getCounter()) {
            Thread.sleep(10);
        }

        publisher.close();
        processor.close();

        System.out.println("Exiting the app");


    }
}
