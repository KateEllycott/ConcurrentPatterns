package com.kateellycott.concurrentpatterns.streams.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmployeeListGenerator {

    private static String firstNames[] = {"Mary","Patricia","Linda",
            "Barbara","Elizabeth","James",
            "John","Robert","Michael",
            "William"};

    private static String lastNames[] = {"Smith","Jones","Taylor",
            "Williams","Brown","Davies",
            "Evans","Wilson","Thomas",
            "Roberts"};

    public static List<Employee> generate(int size) {
        Random random = new Random();
        List<Employee> employees = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            employees.add(new Employee(i, firstNames[random.nextInt(10)] + " " + lastNames[random.nextInt(10)]));
        }
        return employees;
    }
}
