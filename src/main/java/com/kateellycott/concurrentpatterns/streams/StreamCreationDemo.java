package com.kateellycott.concurrentpatterns.streams;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

class Person implements Comparable<Person> {

    private int id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private int salary;
    private double coefficient;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String secondName) {
        this.lastName = secondName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coeficient) {
        this.coefficient = coeficient;
    }

    @Override
    public int compareTo(Person o) {
        if(this == o) {
            return 0;
        }
        int compareLastNames = this.getLastName().compareTo(o.getLastName());

        if(compareLastNames != 0) {
            return compareLastNames;
        }
        else {
            return this.getFirstName().compareTo(o.getFirstName());
        }
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}

class PersonGenerator {

    private static String firstNames[] = {"Mary","Patricia","Linda",
            "Barbara","Elizabeth","James",
            "John","Robert","Michael",
            "William"};
    private static String lastNames[] = {"Smith","Jones","Taylor",
            "Williams","Brown","Davies",
            "Evans","Wilson","Thomas",
            "Roberts"};

    public static List<Person> generate(int size) {
        List<Person> persons = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < size; i++) {
            Person person = new Person();
            person.setId(i);
            person.setFirstName(firstNames[random.nextInt(10)]);
            person.setLastName(lastNames[random.nextInt(10)]);
            person.setSalary(random.nextInt(100000));
            person.setCoefficient(random.nextDouble()*10);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, - random.nextInt(30));
            Date birthDate = calendar.getTime();
            person.setBirthDate(birthDate);
            persons.add(person);
        }
        return persons;
    }
}

class MySupplier implements Supplier<String> {

    private final AtomicInteger counter;

    public MySupplier() {
        counter = new AtomicInteger(0);
    }

    @Override
    public String get() {
        int value = counter.getAndAdd(1);
        return "Value " + value;
    }
}

public class StreamCreationDemo {
    public static void main(String[] args) throws IOException {
        List<Person> persons = PersonGenerator.generate(10000);
        Stream<Person> personStream = persons.parallelStream();
        System.out.printf("Number of persons: %d\n", personStream.count());

        System.out.printf("From a Supplier: \n");
        MySupplier supplier = new MySupplier();
        Stream<String> generatorStream = Stream.generate(supplier);
        generatorStream.parallel().limit(10).forEach(s -> System.out.printf("%s\n", s));

        System.out.printf("From a predefined set of elements:\n");
        Stream<String> elementsStream = Stream.of("Peter", "John", "Mary");
        elementsStream.forEach(element -> System.out.printf("%s\n", element));

        try(BufferedWriter bw = new BufferedWriter(new FileWriter("text.txt"))) {
            bw.write("Hello");
            bw.write("World");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("From a file: \n");
        try(BufferedReader reader = new BufferedReader(new FileReader("text.txt"))) {
            Stream<String> fileLines = reader.lines();
            System.out.printf("Number of lines in the file: %d\n\n", fileLines.parallel().count());
            System.out.printf("\n");
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("From a Directory:\n");
        try {
            Stream<Path> directoryContent = Files.list(Paths.get(System.getProperty("user.home")));
            System.out.printf("Number of elements (files and folders): %d\n\n", directoryContent.parallel().count());
            System.out.printf("***********************************************************\n");
            System.out.printf("\n");
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        System.out.printf("From an Array\n");
        String[] array = {"1", "2", "3", "4", "5", "6"};
        Stream<String> streamFromArray = Arrays.stream(array);
        streamFromArray.parallel().forEach(s -> System.out.printf("%s\n", s));

        System.out.printf("From Random: \n");
        Random random = new Random();
        DoubleStream doubleStream = random.doubles(10);
        double doubleStreamOverage = doubleStream.peek(d -> System.out.printf("%f : ", d)).average().getAsDouble();
        System.out.printf("overage: %f\n", doubleStreamOverage);


        System.out.printf("From streams concatenation \n");
        Stream<String> stream1 = Stream.of("1", "2", "3", "4");
        Stream<String> stream2 = Stream.of("5", "6", "7", "8");
        Stream<String> stream3 = Stream.concat(stream1, stream2);
        stream3.parallel().forEach(s -> System.out.printf("%s\n", s));

        Stream<Path> logPaths = Files.find(Paths.get("D:\\kateellycott"), Integer.MAX_VALUE, (path, attr) -> path.toString().endsWith(".mp3"));
        logPaths.forEach(System.out::println);
    }
}
