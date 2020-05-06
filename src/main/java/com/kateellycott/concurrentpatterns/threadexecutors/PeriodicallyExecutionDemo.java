package com.kateellycott.concurrentpatterns.threadexecutors;

import java.util.Date;
import java.util.concurrent.*;

class PeriodicallyTask implements Runnable {

    private final String name;

    PeriodicallyTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.printf("Task: %s starting..%s\n", name, new Date());
    }
}

public class PeriodicallyExecutionDemo {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        System.out.printf("Main: Started at %s\n", new Date());
        PeriodicallyTask task = new PeriodicallyTask("Task");
        ScheduledFuture<?> future = executor.
                scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);
        for(int i = 0; i < 10; i++) {
            System.out.printf("Main: Delay: %d\n", future.getDelay(TimeUnit.MILLISECONDS));
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        executor.shutdown();

        try {
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Finished at: %s\n", new Date());
    }
}
