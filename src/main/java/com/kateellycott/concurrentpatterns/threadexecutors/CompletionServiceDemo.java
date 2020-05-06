package com.kateellycott.concurrentpatterns.threadexecutors;

import java.util.concurrent.*;

class ReportGenerator implements Callable<String> {
    private final String sender;
    private final String title;

    ReportGenerator(String sender, String title) {
        this.sender = sender;
        this.title = title;
    }

    @Override
    public String call() throws Exception {
        try {
            long duration = (long)(Math.random()*10);
            System.out.printf("%s_%S: Report Generator: Generating a report during" +
                    "%d seconds\n", sender, title, duration);
            TimeUnit.SECONDS.sleep(duration);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        String ret = sender + " : " + title;
        return ret;
    }
}

class ReportRequest implements Runnable {
    private final String name;
    private final CompletionService<String> service;

    ReportRequest(String name, CompletionService<String> service) {
        this.name = name;
        this.service = service;
    }

    @Override
    public void run() {
        ReportGenerator reportGenerator = new ReportGenerator(name, "Report");
        service.submit(reportGenerator);
    }
}

class ReportProcessor implements Runnable {

    private final CompletionService<String> service;
    private volatile boolean end;

    ReportProcessor(CompletionService<String> service) {
        this.service = service;
        end = false;
    }

    @Override
    public void run() {
        while (!end) {
            try {
                Future<String> result = service.poll(20, TimeUnit.SECONDS);
                if (result != null) {
                    String report = result.get();
                    System.out.printf("Report Processor: Report Received: %s\n", report);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Report Sender : End\n");
    }
    void stopProcessing() {
        end = true;
    }
}

public class CompletionServiceDemo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        CompletionService<String> service = new ExecutorCompletionService<>(executor);

        ReportRequest faceRequest = new ReportRequest("Face", service);
        ReportRequest onlineRequest = new ReportRequest("Online", service);
        Thread faceThread = new Thread(faceRequest);
        Thread onlineThread = new Thread(onlineRequest);
        ReportProcessor processor = new ReportProcessor(service);
        Thread senderThread = new Thread(processor);
        System.out.printf("Main: setting the threads\n");

        faceThread.start();
        onlineThread.start();
        senderThread.start();

        try {
            System.out.printf("Main: Waiting for the report generators.\n");
            faceThread.join();
            onlineThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Shutting down the executor.\n");
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        processor.stopProcessing();
        System.out.printf("Main: Ends");
    }
}
