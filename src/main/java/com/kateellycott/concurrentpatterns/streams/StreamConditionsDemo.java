package com.kateellycott.concurrentpatterns.streams;

import java.util.Comparator;
import java.util.List;

public class StreamConditionsDemo {
    public static void main(String[] args) {

        List<Person> people = PersonGenerator.generate(10);

        long minSalary = people.parallelStream().map(Person::getSalary).min(Integer::compare).get();
        long maxSalary = people.parallelStream().mapToInt(Person::getSalary).max().getAsInt();
        System.out.printf("Salaries are between %s and %s\n", minSalary, maxSalary);

        boolean condition = people.parallelStream().mapToInt(Person::getSalary).allMatch(s -> s > 0);
        System.out.printf("Salary > 0: %s\n", condition);

        boolean condition1 = people.parallelStream().mapToInt(Person::getSalary).allMatch(s -> s > 10000);
        System.out.printf("Salary > 10000: %s\n", condition1);

        boolean condition2 = people.parallelStream().mapToInt(Person::getSalary).allMatch(s -> s < 30000);
        System.out.printf("Salary < 30000: %s\n", condition2);

        boolean condition3 = people.parallelStream().mapToInt(Person::getSalary).anyMatch(s -> s > 50000);
        System.out.printf("Any with salary > 50000: %s\n", condition3);

        boolean condition4 = people.parallelStream().mapToInt(Person::getSalary).anyMatch(s -> s > 100000);
        System.out.printf("Any with salary > 1000000: %s\n",condition4);

        boolean condition5 = people.parallelStream().mapToInt(Person::getSalary).noneMatch(s -> s > 100000);
        System.out.printf("None wit salary > 1000000: %s\n", condition5);

        Person person = people.parallelStream().findAny().get();
        System.out.printf("Any: %s %s %d\n", person.getFirstName(), person.getLastName(), person.getSalary());

        Person person1 = people.parallelStream().findFirst().get();
        System.out.printf("First: %s %s %d\n", person1.getFirstName(), person1.getLastName(), person1.getSalary());

        Person person2 = people.parallelStream().sorted(Comparator.comparingInt(Person::getSalary)).findFirst().get();
        System.out.printf("A person with the lowest salary: %s %s %d\n",
                person2.getFirstName(), person2.getLastName(), person2.getSalary());




    }
}
