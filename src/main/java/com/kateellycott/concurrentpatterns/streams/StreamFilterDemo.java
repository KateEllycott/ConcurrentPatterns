package com.kateellycott.concurrentpatterns.streams;

import java.util.Arrays;
import java.util.List;

public class StreamFilterDemo {
    public static void main(String[] args) {

        List<Person> people = PersonGenerator.generate(10);
        people.parallelStream().forEach(p -> System.out.println(p.getFirstName() + " " + p.getLastName()));

        people.parallelStream().distinct().forEach(p -> System.out.println(p.getFirstName() + " " + p.getLastName()));

        Integer[] numbers = {1, 5, 6, 8, 9, 9, 0, 7, 5};
        Arrays.asList(numbers).parallelStream().mapToInt(n -> n)
                .distinct().forEach(p -> System.out.println("Number: " + p));

        people.parallelStream().filter(p -> p.getSalary() < 3000)
                .forEach(p -> System.out.println("Person: " + p.getLastName() + " " + p.getLastName()
                + ", salary: " + p.getSalary()));

        Arrays.asList(numbers).parallelStream().mapToInt(p -> p).filter(p -> p < 2)
                .forEach(p -> System.out.println(p));

        people.parallelStream().mapToDouble(p -> p.getSalary()).sorted()
                .limit(5).forEach(s -> System.out.println("limit: " + s));

        people.parallelStream().mapToDouble(p -> p.getSalary()).sorted()
                .skip(5).forEach(s -> System.out.println("Skip: " + s));

    }
}
