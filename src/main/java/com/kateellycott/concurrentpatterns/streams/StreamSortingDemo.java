package com.kateellycott.concurrentpatterns.streams;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamSortingDemo {
    public static void main(String[] args) {
        int[] numbers = {1, 5, 8, 9, 7, 3, 6, 8, 3, 2};
        Arrays.stream(numbers).parallel().sorted().forEachOrdered(n -> System.out.println(n));

        List<Person> people = PersonGenerator.generate(100);
        people.parallelStream().sorted().forEachOrdered(p -> System.out.println(p.getFirstName() + " " + p.getLastName()));

        Set<Person> personSet = new TreeSet<>(people);
        for(int i = 0; i < 10; i++) {
            Person person = personSet.stream().parallel().limit(1).collect(Collectors.toList()).get(0);
            System.out.println("Person: "+ person.getFirstName() + " " + person.getLastName());
        }

        for(int i = 0; i < 10; i++) {
            Person person = personSet.stream().parallel().unordered().limit(1).collect(Collectors.toList()).get(0);
            System.out.println("(unordered) Person: "+ person.getFirstName() + " " + person.getLastName());
        }



    }
}
