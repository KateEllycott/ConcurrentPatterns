package com.kateellycott.concurrentpatterns.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.DoubleStream;

class DoubleGenerator {

    public static  List<Double> generateDoubleList(int size, int max) {
        List<Double> doubleList = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < size; i++) {
            doubleList.add(random.nextDouble()*max);
        }
        return doubleList;
    }

    public static DoubleStream generateStreamFromList(List<Double> doubleList) {
        DoubleStream.Builder builder = DoubleStream.builder();
        for(Double number: doubleList) {
            builder.add(number);
        }
        return builder.build();
    }
}

class Point {

    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}

class PointGenerator {

    public static List<Point> generatePointList(int size) {

        List<Point> points = new ArrayList<>();
        Random random = new Random();

        for(int i = 0; i < size; i++) {
            Point point = new Point();
            point.setX(random.nextDouble());
            point.setY(random.nextDouble());
            points.add(point);
        }
        return points;
    }
}

public class MapReduceModelDemo {
    public static void main(String[] args) {

        List<Double> numbers = DoubleGenerator.generateDoubleList(10_000, 1000);
        DoubleStream doubleStream = DoubleGenerator.generateStreamFromList(numbers);
        long numberOfElements = doubleStream.parallel().count();
        System.out.printf("The list of numbers has %d elements.\n", numberOfElements);

        doubleStream = DoubleGenerator.generateStreamFromList(numbers);
        double sum = doubleStream.parallel().sum();
        System.out.printf("The sum of the list of numbers is %f\n", sum);

        doubleStream = DoubleGenerator.generateStreamFromList(numbers);
        double average = doubleStream.parallel().average().getAsDouble();
        System.out.printf("The average of the numbers is %f\n", average);

        doubleStream = DoubleGenerator.generateStreamFromList(numbers);
        double max = doubleStream.parallel().max().getAsDouble();
        System.out.printf("The maximum value in the list is %f\n", max);

        doubleStream = DoubleGenerator.generateStreamFromList(numbers);
        double min= doubleStream.parallel().min().getAsDouble();
        System.out.printf("The minimum value in the list is %f\n", min);

        System.out.printf("Reduce, first version\n");
        List<Point> pointList = PointGenerator.generatePointList(10000);
        Optional<Point> point = pointList.parallelStream().reduce((p1, p2) -> {
            Point ret = new Point();
            ret.setX(p1.getX() + p2.getX());
            ret.setY(p1.getY() + p2.getY());
            return ret;
        });
         System.out.printf("Sum Point: x = %f, y = %f\n", point.get().getX(), point.get().getY());

        System.out.printf("Reduce, second version\n");
        List<Person> personList = PersonGenerator.generate(10_000);
        long totalSalary = personList.parallelStream().map((p) -> p.getSalary()).reduce(0, (s1,s2) -> s1+s2);
        System.out.printf("Total salary: %d\n", totalSalary);

        System.out.printf("Reduce: third version:\n");
        Integer value = 0;
        value = personList.stream().reduce(value, (n, p) -> {
            System.out.println("n = " + n);
            if(p.getSalary() > 50_000) {
                System.out.println("  > 50_000 " + (n + 1));
                return n+1;

            }
            else {
                System.out.println("  < 50_000 " + n );
                return n;
            }
        }, (n1, n2) -> n1+n2 );
        System.out.printf("The number of people with a salary bigger than 50 000 is %d\n", value);
    }
}
