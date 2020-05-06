package com.kateellycott.concurrentpatterns.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class Counter {

    private String value;
    private int counter;

    public String getValue() {
        return value;
    }

    public int getCounter() {
        return counter;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void increment() {
        counter++;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}

public class CollectDemo {
    public static void main(String[] args) {

        List<Person> persons = PersonGenerator.generate(10_000);

        Map<String, List<Person>> personsByName = persons.parallelStream().collect(Collectors.groupingByConcurrent(Person::getFirstName));

        personsByName.keySet().forEach(key -> {
            List<Person> listOfPersons = personsByName.get(key);
            System.out.printf("%s: There are %d persons with that name\n", key, listOfPersons.size());
        });

        String allNames = persons.parallelStream().map(p -> p.toString()).collect(Collectors.joining(", "));
        System.out.printf("%s: \n", allNames);

        Map<Boolean, List<Person>> personsBySalary = persons.parallelStream().collect(Collectors.partitioningBy(p -> p.getSalary() >= 50_000));
        personsBySalary.keySet().forEach(key -> {
            List<Person> listOfPersons = personsBySalary.get(key);
            System.out.printf("%s: %d \n", key, listOfPersons.size());
        });

        Map<String, String> nameMap = persons.parallelStream()
        .collect(Collectors.toConcurrentMap(p -> p.getFirstName(), p -> p.getLastName(), (s1, s2) -> s1 + ", " + s2));
        nameMap.forEach((key,value) -> {
            System.out.printf("%S: %s \n", key, value);
        });

        System.out.println("Collect, first example");

        List<Person> highSalaryPeople = persons.parallelStream().collect(ArrayList::new, (list, person) -> {
            if(person.getSalary() > 50_000) {
                list.add(person);
            }
        }, ArrayList::addAll);
        System.out.printf("High Salary People: %d\n", highSalaryPeople.size());

        System.out.println("Collect, second example: ");
        Map<String, Counter> peopleNames = persons.parallelStream()
                .collect(ConcurrentHashMap::new,
                        (hash, person) -> {
                            hash.computeIfPresent(person.getFirstName(),
                                    (name, counter) -> {
                                counter.increment();
                                return counter;
                                    });
                            hash.computeIfAbsent(person.getFirstName(), (name) -> new Counter());
                        },
                        (hash1, hash2) -> {
                            hash2.forEach((key, value) -> {
                                hash1.merge(key, value, (v1, v2) -> {
                                    v1.setCounter(v1.getCounter() + v2.getCounter());
                                    return v1;
                                });
                            });
                        }
                );
        peopleNames.forEach((name, counter) -> System.out.printf("Name: %s, Counter: %d\n", name, counter.getCounter()));

        

    }
}
