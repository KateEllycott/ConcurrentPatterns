package com.kateellycott.concurrentpatterns.streams;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

class BasicPerson {

    private String name;
    private Long age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }
}

class FileGenerator {

    public static List<String> generateFile(int size) {
        List<String> file = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            file.add("Lorem ipsum dolor sit amet, " +
                            "consectetur adipiscing elit. Morbi lobortis" +
                            "cursus venenatis. Mauris tempus elit ut" +
                            "malesuada luctus. Interdum et malesuada fames" +
                            "ac ante ipsum primis in faucibus. Phasellus" +
                            "laoreet sapien eu pulvinar rhoncus. Integer vel" +
                            "ultricies leo. Donec vel sagittis nibh. " +
                    "Maecenas eu quam non est hendrerit pu");
        }
        return file;
    }
}

public class StreamTransformDemo {
    public static void main(String[] args) {

        List<Person> people = PersonGenerator.generate(100);
        DoubleStream ds = people.parallelStream().mapToDouble(p -> p.getSalary());
        ds.distinct().forEach(d -> System.out.println("Salary: "  + d));

        ds = people.parallelStream().mapToDouble(p -> p.getSalary());
        long size = ds.count();
        System.out.println("Size: " + size);

        List<BasicPerson> basicPeople = people.parallelStream().map(p -> {
            BasicPerson person = new BasicPerson();
            person.setName(p.getFirstName() + " " + p.getLastName());
            person.setAge(p.getAge());
            return person;
        }).collect(Collectors.toList());

        basicPeople.forEach(p -> System.out.println("Name: " + p.getName() + " Age: " + p.getAge()));

        List<String> list = FileGenerator.generateFile(100);
        Map<String, Long> wordCount = list.parallelStream().flatMap(str -> Stream.of(str.split(" ")))
                .filter(str -> str.length() > 0).sorted().collect(Collectors.groupingByConcurrent(e -> e, Collectors.counting()));
        wordCount.forEach((w, c) -> System.out.println("Word: " + w + " count: " + c ));




    }
}
