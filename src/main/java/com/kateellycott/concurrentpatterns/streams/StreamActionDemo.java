package com.kateellycott.concurrentpatterns.streams;

import java.util.List;

public class StreamActionDemo {

    public static void main(String[] args) {

        List<Person> people = PersonGenerator.generate(10);
        people.parallelStream().forEach((person) -> System.out.println(person.getLastName() + " " + person.getFirstName()));
        List<Double> doubles = DoubleGenerator.generateDoubleList(10, 100);
        System.out.println("Parallel forEachOrdered with numbers:\n");
        doubles.parallelStream().sorted().forEachOrdered(System.out::println);

        System.out.println("Parallel forEach with numbers:\n");
        doubles.parallelStream().sorted().forEach(System.out::println);

        doubles.parallelStream().peek((p) -> System.out.println("Step 1: Number: " + p))
                .peek((p) -> System.out.println("Step 2: Number: " + p))
                .forEach((p) -> System.out.println("Final Step: Number: " + p));
    }

}
