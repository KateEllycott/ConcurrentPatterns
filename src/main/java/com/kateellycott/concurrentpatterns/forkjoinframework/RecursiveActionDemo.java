package com.kateellycott.concurrentpatterns.forkjoinframework;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

class Product {
    private String name;
    private double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

class ProductGenerator {

    int listSize ;

    ProductGenerator(int listSize) {
        this.listSize = listSize;
    }

    List<Product> generate() {
        List<Product> products = null;
        if(listSize > 0) {
            products = new ArrayList<>(listSize);
            for(int i = 0; i < listSize; i++) {
                Product product = new Product();
                product.setName("Product" + i);
                product.setPrice(10);
                products.add(product);
            }
        }
        return products;
    }
}

class Task extends RecursiveAction {

    private List<Product> products;
    private int first;
    private int last;
    private double increment;

    Task(List<Product> products, int first, int last, double increment) {
        this.products = products;
        this.first = first;
        this.last = last;
        this.increment = increment;
    }

    @Override
    protected void compute() {
        if(last - first < 10) {
            updatePrices();
        } else {
            int middle = (last + first)/2;
            System.out.printf("Task: Pending tasks: %s\n", getQueuedTaskCount());
            System.out.println("\n");
            Task t1 = new Task(products, first, middle + 1, increment);
            Task t2 = new Task(products, middle + 1, last , increment);
            invokeAll(t1, t2);
        }
    }

    private void updatePrices() {
        for(int i = first; i < last; i++) {
            Product product = products.get(i);
            product.setPrice(product.getPrice() * (1 + increment));
        }
    }
}

public class RecursiveActionDemo {
    public static void main(String[] args) {
        ProductGenerator generator = new ProductGenerator(10000);
        List<Product> products = generator.generate();

        Task task = new Task(products,0, products.size(), 0.20);
        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);


        do {
            System.out.printf("Main: Thread Count: %d\n", pool.getActiveThreadCount());
            System.out.printf("Main: Thread Steal: %d\n", pool.getStealCount());
            System.out.printf("Main: Parallelism: %d\n", pool.getParallelism());
            System.out.println("\n");

            try {
                TimeUnit.MILLISECONDS.sleep(5);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());

        pool.shutdown();

        if(task.isCompletedNormally()) {
            System.out.printf("Main: The process has completed normally\n");
        }

        for(int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if(product.getPrice() != 12) {
                System.out.printf("Product :  %s: %f\n", product.getName(), product.getPrice());
            }
        }

        System.out.printf("Main: End of the program.\n");
    }
}
